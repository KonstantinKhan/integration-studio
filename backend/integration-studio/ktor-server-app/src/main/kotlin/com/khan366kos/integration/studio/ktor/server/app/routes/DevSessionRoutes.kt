package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.Login
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.common.models.auth.simple.StorageId
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import com.khan366kos.integration.studio.ktor.server.app.UserSession
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SeedSessionResponse(
    val sessionId: String,
    val expiresAt: Long,
    val username: String
)

@Serializable
data class EnvMissingResponse(
    val error: String,
    val missing: List<String>
)

/**
 * DEV-ONLY. Активируется только при IS_DEV=true.
 *
 * Засевает тестовую сессию в SessionStore из env-переменных (login/password),
 * выполняя реальный signIn против Polynom. Удобно для тестов из Postman:
 * один пустой POST — cookie USER_SESSION в jar, дальше идут реальные
 * защищённые маршруты через SessionInterceptorPlugin.
 *
 * ВНИМАНИЕ: никогда не включать в проде. Эндпоинт принимает любые креды из env
 * и автоматически сеет сессию без других проверок.
 */
fun Application.devSessionRoute(config: AppConfig) {
    if (System.getenv("IS_DEV") != "true") return

    log.warn("DEV session seeding enabled — DO NOT USE IN PRODUCTION")

    routing {
        route("/dev") {
            post("/seed-session") {
                val login = System.getenv("TEST_LOGIN")
                val password = System.getenv("TEST_PASSWORD")
                val storageId = System.getenv("TEST_STORAGE_ID")

                val missing = buildList {
                    if (login.isNullOrBlank()) add("TEST_LOGIN")
                    if (password.isNullOrBlank()) add("TEST_PASSWORD")
                    if (storageId.isNullOrBlank()) add("TEST_STORAGE_ID")
                }
                if (missing.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        EnvMissingResponse(error = "Отсутствуют env-переменные", missing = missing)
                    )
                    return@post
                }

                try {
                    val response = config.polynomApplicationService.signIn(
                        LoginRequest(
                            storageId = storageId,
                            password = password,
                            login = login
                        )
                    )

                    val sessionId = UUID.randomUUID().toString()
                    val now = System.currentTimeMillis()
                    val credentials = UserCredentials(
                        login = Login(login),
                        storageId = StorageId(storageId),
                        accessToken = AccessToken(response.accessToken),
                        refreshToken = RefreshToken(response.refreshToken),
                        issuedAt = now,
                        expiresAt = now + (response.expiresIn * 1000L)
                    )

                    config.sessionStore.store(sessionId, credentials)
                    call.sessions.set(UserSession(id = sessionId, username = login))

                    log.info(
                        "Dev session seeded: sessionId={}, login={}, expiresAt={}",
                        sessionId, login, credentials.expiresAt
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        SeedSessionResponse(
                            sessionId = sessionId,
                            expiresAt = credentials.expiresAt,
                            username = login
                        )
                    )
                } catch (e: Exception) {
                    application.log.error("Dev seed-session signIn error: ${e.message}", e)
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "Ошибка signIn: ${e.message}")
                    )
                }
            }
        }
    }
}
