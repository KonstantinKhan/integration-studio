package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.domain.models.auth.UserCredentials
import com.khan366kos.domain.models.auth.simple.AccessToken
import com.khan366kos.domain.models.auth.simple.Login
import com.khan366kos.domain.models.auth.simple.RefreshToken
import com.khan366kos.domain.models.auth.simple.StorageId
import com.khan366kos.integration.studio.ktor.server.app.UserSession
import com.khan366kos.integration.studio.ktor.server.app.config.AppSettings
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.config.AppSettingsStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import java.security.MessageDigest
import java.util.UUID

@Serializable
data class ConnectionStatusDto(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val status: String,
    val latencyMs: Long? = null,
    val error: String? = null,
)

@Serializable
data class ConnectionAuthRequest(
    val password: String = "",
)

fun Route.connections(config: AppConfig): Route = route("/connections") {

    // Status page: real manager state for Postgres/RabbitMQ, TCP probe for
    // the HTTP/SMTP services.
    get {
        val settings = config.appSettingsStore.currentSettings

        val db = parseDbAddress(settings.database.url)
        val rabbit = settings.rabbitmq

        val connections = coroutineScope {
            val polynomCheck = async { checkTcpOf(settings.polynom.baseUrl) }
            val loodsmanCheck = async { checkTcpOf(settings.loodsman.baseUrl) }
            val polynomAddr = parseUri(settings.polynom.baseUrl, "polynom").let { it.host.orEmpty() to portOf(it) }
            val loodsmanAddr = parseUri(settings.loodsman.baseUrl, "loodsman").let { it.host.orEmpty() to portOf(it) }

            buildList {
                add(polynomCheck.await().toDto("polynom", "Polynom API", polynomAddr.first, polynomAddr.second))
                add(loodsmanCheck.await().toDto("loodsman", "Loodsman API", loodsmanAddr.first, loodsmanAddr.second))

                val dbStatus = config.databaseManager.status.status
                add(
                    ConnectionStatusDto(
                        id = "postgres",
                        name = "PostgreSQL",
                        host = db.first,
                        port = db.second,
                        status = wireStatus(dbStatus.state),
                        latencyMs = dbStatus.latencyMs,
                        error = dbStatus.lastError,
                    )
                )

                val rabbitStatus = config.rabbitManager.status.status
                add(
                    ConnectionStatusDto(
                        id = "rabbitmq",
                        name = "RabbitMQ",
                        host = rabbit.host,
                        port = rabbit.port,
                        status = wireStatus(rabbitStatus.state),
                        latencyMs = rabbitStatus.latencyMs,
                        error = rabbitStatus.lastError,
                    )
                )

                val email = settings.email
                if (email.enabled && email.smtpHost.isNotBlank()) {
                    val smtpCheck = async { checkTcp(email.smtpHost, email.smtpPort) }
                    add(smtpCheck.await().toDto("smtp", "SMTP", email.smtpHost, email.smtpPort))
                } else {
                    add(
                        ConnectionStatusDto(
                            id = "smtp",
                            name = "SMTP",
                            host = email.smtpHost,
                            port = email.smtpPort,
                            status = "disabled",
                        )
                    )
                }
            }
        }

        call.respond(HttpStatusCode.OK, connections)
    }

    // Local admin login: allows configuring connections from the UI even
    // when every external service (including Polynom, which normally backs
    // authentication) is down. Password comes from APP_ADMIN_PASSWORD.
    post("/auth") {
        val configured = config.environment.config.propertyOrNull("app.admin-password")?.getString()
        if (configured.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "APP_ADMIN_PASSWORD не задан — локальный вход в настройки недоступен")
            )
            return@post
        }

        val body = call.receive<ConnectionAuthRequest>()
        val matches = MessageDigest.isEqual(
            body.password.toByteArray(Charsets.UTF_8),
            configured.toByteArray(Charsets.UTF_8),
        )
        if (!matches) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Неверный пароль"))
            return@post
        }

        val sessionId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        config.sessionStore.store(
            sessionId,
            UserCredentials(
                login        = Login("admin"),
                storageId    = StorageId(""),
                accessToken  = AccessToken(""),
                refreshToken = RefreshToken(""),
                issuedAt     = now,
                expiresAt    = now + 7 * 24 * 60 * 60 * 1000L,
            )
        )
        call.sessions.set(UserSession(id = sessionId, username = "admin"))
        call.respond(HttpStatusCode.OK, mapOf("username" to "admin"))
    }

    // Current settings for the UI. Secrets are blanked; the UI sends them
    // back blank to keep the stored value.
    get("/settings") {
        call.respond(HttpStatusCode.OK, maskedSettings(config))
    }

    // Applies new settings: persists them, hot-swaps what can be swapped
    // immediately (HTTP clients, email config) and triggers background
    // reconnects for Postgres/RabbitMQ.
    put("/settings") {
        val update = call.receive<AppSettingsStore.AppSettingsUpdate>()

        try {
            validateUpdate(update)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid settings")))
            return@put
        }

        val next = config.appSettingsStore.applyUpdate(update)

        config.rebuildPolynomClient(next.polynom)
        config.rebuildLoodsmanClient(next.loodsman)
        config.emailNotifier.updateConfig(next.email)
        config.requestServicesReconnect()

        call.respond(HttpStatusCode.OK, maskedSettings(config))
    }
}

/** Settings for the UI with secrets blanked; blank on PUT keeps the stored value. */
private fun maskedSettings(config: AppConfig): AppSettings {
    val s = config.appSettingsStore.currentSettings
    return AppSettings(
        database = s.database.copy(password = ""),
        rabbitmq = s.rabbitmq.copy(password = ""),
        email = s.email.copy(password = ""),
        scheduler = s.scheduler.copy(servicePassword = ""),
        polynom = s.polynom,
        loodsman = s.loodsman,
    )
}

private fun validateUpdate(update: AppSettingsStore.AppSettingsUpdate) {
    update.polynom?.let { parseUri(it.baseUrl, "polynom.baseUrl") }
    update.loodsman?.let { parseUri(it.baseUrl, "loodsman.baseUrl") }
}

private fun parseUri(raw: String, settingName: String): URI {
    val uri = URI(raw)
    require(uri.scheme in setOf("http", "https")) { "$settingName must use http or https scheme, got: $raw" }
    require(!uri.host.isNullOrBlank()) { "$settingName must contain a host, got: $raw" }
    return uri
}

private suspend fun checkTcpOf(rawBaseUrl: String): Pair<Long?, String?> {
    val uri = runCatching { URI(rawBaseUrl) }.getOrNull()
        ?: return null to "Invalid base URL: $rawBaseUrl"
    val port = when {
        uri.port > 0 -> uri.port
        uri.scheme == "https" -> 443
        else -> 80
    }
    return checkTcp(uri.host ?: "", port)
}

private fun Pair<Long?, String?>.toDto(
    id: String,
    name: String,
    host: String,
    port: Int,
): ConnectionStatusDto = ConnectionStatusDto(
    id = id,
    name = name,
    host = host,
    port = port,
    status = if (first != null) "connected" else "unreachable",
    latencyMs = first,
    error = second,
)

/** Maps internal manager state to the wire contract used by the UI. */
private fun wireStatus(state: String): String = when (state.lowercase()) {
    "connected" -> "connected"
    "connecting" -> "connecting"
    else -> "unreachable"
}

private fun portOf(uri: URI): Int = when {
    uri.port > 0 -> uri.port
    uri.scheme == "https" -> 443
    else -> 80
}

private fun parseDbAddress(url: String): Pair<String, Int> {
    val address = url.removePrefix("jdbc:postgresql://").substringBefore("/")
    val host = address.substringBefore(":")
    val port = address.substringAfterLast(':').substringBefore('?').toIntOrNull() ?: 5432
    return host to port
}

private suspend fun checkTcp(host: String, port: Int): Pair<Long?, String?> =
    withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 3000)
                System.currentTimeMillis() - start to null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null to (e.message ?: e.javaClass.simpleName)
        }
    }
