package com.khan366kos.integration.studio.ktor.server.app

import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.Login
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.common.models.auth.simple.StorageId
import com.khan366kos.integration.studio.transport.models.AuthorizationRequestTransport
import com.khan366kos.etl.excel.service.ManagedWorkbookResult
import com.khan366kos.etl.excel.service.dsl.function.useManagedWorkbook
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.SessionInterceptorPlugin
import com.khan366kos.integration.studio.ktor.server.app.plugins.userCredentials
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.etl.mapper.toEtlWorkbookTransport
import com.khan366kos.etl.polynom.bff.auth.CredentialsContext
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File
import java.nio.file.Files
import java.util.UUID

@Serializable
data class SessionCheckResponse(
    val authenticated: Boolean,
    val sessionId: String? = null,
    val username: String? = null
)

fun Application.configureRouting(config: AppConfig) {
    routing {
        // Public endpoints

        get("/storage-definitions") {
            try {
                val storageDefinitions = config.polynomClient.storageDefinitions()
                call.respond(HttpStatusCode.OK, storageDefinitions)
            } catch (e: Exception) {
                application.log.error("Error fetching storage definitions: ${e.message}", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Ошибка получения storage definitions: ${e.message}")
                )
            }
        }

        post("/authorize") {
            try {
                val authRequest = call.receive<AuthorizationRequestTransport>()

                val sessionId = UUID.randomUUID().toString()
                val userSession = UserSession(
                    id = sessionId,
                    username = authRequest.username
                )

                val response = config.polynomClient.signIn(
                    LoginRequest(
                        storageId = authRequest.storageId,
                        password = authRequest.password,
                        login = authRequest.username
                    )
                )

                val now = System.currentTimeMillis()
                val credentials = UserCredentials(
                    login = Login(authRequest.username),
                    storageId = StorageId(authRequest.storageId),
                    accessToken = AccessToken(response.accessToken),
                    refreshToken = RefreshToken(response.refreshToken),
                    issuedAt = now,
                    expiresAt = now + (response.expiresIn * 1000L)
                )

                config.sessionStore.store(sessionId, credentials)
                call.sessions.set(userSession)

                call.respond(
                    HttpStatusCode.OK,
                    mapOf("message" to "Авторизация успешна", "storageId" to authRequest.storageId)
                )
            } catch (e: Exception) {
                application.log.error("Authorization error: ${e.message}", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Ошибка авторизации: ${e.message}")
                )
            }
        }

        get("/check-session") {
            val session = call.sessions.get<UserSession>()
            if (session != null) {
                call.respond(
                    SessionCheckResponse(
                        authenticated = true,
                        sessionId = session.id,
                        username = session.username
                    )
                )
            } else {
                call.respond(HttpStatusCode.Unauthorized, SessionCheckResponse(authenticated = false))
            }
        }

        post("/upload") {
            val multipartData = call.receiveMultipart()
            var fileName: String? = null
            var tempFile: File? = null

            try {
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName ?: "uploaded.xlsx"
                            tempFile = Files.createTempFile("upload_", "_${fileName}").toFile()

                            @Suppress("DEPRECATION")
                            part.streamProvider().use { input ->
                                tempFile!!.writeBytes(input.readBytes())
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                if (tempFile == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Файл не найден"))
                    return@post
                }

                val result = useManagedWorkbook {
                    path = tempFile.absolutePath
                }

                when (result) {
                    is ManagedWorkbookResult.Success -> {
                        call.respond(result.etlWorkbook.toEtlWorkbookTransport())
                    }
                    is ManagedWorkbookResult.Failure -> {
                        call.respond(
                            HttpStatusCode.UnprocessableEntity,
                            mapOf("error" to "Ошибка обработки файла: ${result.exception.message}")
                        )
                    }
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Ошибка сервера: ${e.message}")
                )
            } finally {
                tempFile?.delete()
            }
        }

        get("/") {
            call.respondText("Hello World!")
        }

        // Protected endpoints
        route("/") {
            install(SessionInterceptorPlugin) {
                sessionStore = config.sessionStore
            }

            get("/references") {
                try {
                    val references = withContext(
                        CredentialsContext(call.userSession.id, call.userCredentials)
                    ) {
                        config.polynomClient.getReference()
                    }
                    call.respond(HttpStatusCode.OK, references)
                } catch (e: Exception) {
                    application.log.error("Error fetching references: ${e.message}", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Ошибка получения справочников: ${e.message}")
                    )
                }
            }
        }
    }
}