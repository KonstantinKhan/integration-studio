package com.khan366kos.integration.studio.ktor.server.app.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI

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

fun Route.connections(environment: ApplicationEnvironment): Route = route("/connections") {
    get {
        val config = environment.config

        val polynomUri = URI(config.property("polynom.base-url").getString())
        val polynomHost = polynomUri.host ?: ""
        val polynomPort = when {
            polynomUri.port > 0 -> polynomUri.port
            polynomUri.scheme == "https" -> 443
            else -> 80
        }

        val loodsmanUri = URI(config.property("loodsman.base-url").getString())
        val loodsmanHost = loodsmanUri.host ?: ""
        val loodsmanPort = when {
            loodsmanUri.port > 0 -> loodsmanUri.port
            loodsmanUri.scheme == "https" -> 443
            else -> 80
        }

        val dbAddress = config.property("database.url").getString()
            .removePrefix("jdbc:postgresql://")
            .substringBefore("/")
        val dbHost = dbAddress.substringBefore(":")
        val dbPort = parseDbPort(dbAddress)

        val rabbitHost = config.property("rabbitmq.host").getString()
        val rabbitPort = config.property("rabbitmq.port").getString().toInt()

        val smtpEnabled = config.propertyOrNull("email.enabled")?.getString()?.toBoolean() ?: false
        val smtpHost = config.propertyOrNull("email.smtp-host")?.getString() ?: ""
        val smtpPort = config.propertyOrNull("email.smtp-port")?.getString()?.toInt() ?: 587

        val connections = coroutineScope {
            val polynomCheck = async { checkTcp(polynomHost, polynomPort) }
            val loodsmanCheck = async { checkTcp(loodsmanHost, loodsmanPort) }
            val postgresCheck = async { checkTcp(dbHost, dbPort) }
            val rabbitmqCheck = async { checkTcp(rabbitHost, rabbitPort) }
            val smtpCheck = if (smtpEnabled && smtpHost.isNotBlank()) async { checkTcp(smtpHost, smtpPort) } else null

            buildList {
                add(polynomCheck.await().toDto("polynom", "Polynom API", polynomHost, polynomPort))
                add(loodsmanCheck.await().toDto("loodsman", "Loodsman API", loodsmanHost, loodsmanPort))
                add(postgresCheck.await().toDto("postgres", "PostgreSQL", dbHost, dbPort))
                add(rabbitmqCheck.await().toDto("rabbitmq", "RabbitMQ", rabbitHost, rabbitPort))
                if (smtpCheck != null) {
                    add(smtpCheck.await().toDto("smtp", "SMTP", smtpHost, smtpPort))
                } else {
                    add(
                        ConnectionStatusDto(
                            id = "smtp",
                            name = "SMTP",
                            host = smtpHost,
                            port = smtpPort,
                            status = "disabled",
                        )
                    )
                }
            }
        }

        call.respond(HttpStatusCode.OK, connections)
    }
}

private fun Pair<Long?, String?>.toDto(id: String, name: String, host: String, port: Int): ConnectionStatusDto =
    ConnectionStatusDto(
        id = id,
        name = name,
        host = host,
        port = port,
        status = if (first != null) "connected" else "unreachable",
        latencyMs = first,
        error = second,
    )

private fun parseDbPort(address: String): Int =
    address.substringAfterLast(':').substringBefore('?').toIntOrNull() ?: 5432

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
