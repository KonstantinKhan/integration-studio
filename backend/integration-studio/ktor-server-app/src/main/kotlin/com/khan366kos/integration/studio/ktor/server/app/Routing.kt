package com.khan366kos.integration.studio.ktor.server.app

import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.Login
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.common.models.auth.simple.StorageId
import com.khan366kos.common.models.business.GroupContent
import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.business.Owner
import com.khan366kos.integration.studio.transport.models.AuthorizationRequestTransport
import com.khan366kos.etl.excel.service.ManagedWorkbookResult
import com.khan366kos.etl.excel.service.dsl.function.useManagedWorkbook
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.SessionInterceptorPlugin
import com.khan366kos.integration.studio.ktor.server.app.plugins.userCredentials
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.etl.mapper.toEtlWorkbookTransport
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import com.khan366kos.integration.studio.transport.models.IdentifiableObjectTransport
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
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
        get("/storage-definitions") {
            try {
                val storageDefinitions = config.polynomApplicationService.storageDefinitions()
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

                val response = config.polynomApplicationService.signIn(
                    LoginRequest(
                        storageId = authRequest.storageId,
                        password = authRequest.password,
                        login = authRequest.username
                    )
                )

                val now = System.currentTimeMillis()
                val credentials = com.khan366kos.common.models.auth.UserCredentials(
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

        route("/") {
            install(SessionInterceptorPlugin) {
                sessionStore = config.sessionStore
            }
            route("references") {
                post("/create") {
                    try {
                        val request = call.receive<CreateReferenceCommand>()
                        val reference = config.polynomApplicationService.referenceCreate(call.userSession.id, request)
                        call.respond(HttpStatusCode.Created, reference)
                    } catch (e: Error) {
                        println("Error: ${e.message}")
                    }
                }
                post("/delete") {
                    try {
                        val request = call.receive<DeleteReferenceCommand>()
                        val response = config.polynomApplicationService.referenceDelete(call.userSession.id, request)
                        call.respond(response.status)
                    } catch (e: Error) {
                        println("Error: ${e.message}")
                        call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                    }
                }
                get {
                    try {
                        val typeId = call.parameters["typeId"]?.toInt()
                        val objectId = call.parameters["objectId"]?.toInt()

                        if (typeId == null && objectId == null) {
                            val references = config.polynomApplicationService.references(call.userSession.id)
                            call.respond(HttpStatusCode.OK, references)
                        } else {
                            val reference = config.polynomApplicationService.reference(
                                call.userSession.id,
                                IdentifiableObjectTransport(
                                    objectId!!,
                                    typeId!!
                                )
                            )
                            call.respond(HttpStatusCode.OK, reference)
                        }
                    } catch (e: Exception) {
                        application.log.error("Error fetching references: ${e.message}", e)
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "Ошибка получения справочников: ${e.message}")
                        )
                    }
                }
            }
            route("catalogs") {
                get {
                    try {
                        val referenceTypeId = call.parameters["referenceTypeId"]?.toInt()
                        val referenceObjectId = call.parameters["referenceObjectId"]?.toInt()
                        val typeId = call.parameters["typeId"]?.toInt()
                        val objectId = call.parameters["objectId"]?.toInt()

                        if (typeId == null && objectId == null) {
                            val catalogs = config.polynomApplicationService.catalogs(
                                call.userSession.id,
                                IdentifiableObjectTransport(
                                    referenceObjectId!!,
                                    referenceTypeId!!
                                )
                            )
                            call.respond(HttpStatusCode.OK, catalogs)
                        } else {
                            val catalog = config.polynomApplicationService.catalog(
                                call.userSession.id,
                                IdentifiableObjectTransport(
                                    objectId!!,
                                    typeId!!
                                )
                            )
                            call.respond(HttpStatusCode.OK, catalog)
                        }

                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "Ошибка получения каталогов: ${e.message}")
                        )
                    }
                }
            }
            route("groups") {
                get {
                    try {
                        val catalogTypeId = call.parameters["catalogTypeId"]?.toInt()
                        val catalogObjectId = call.parameters["catalogObjectId"]?.toInt()
                        val groupTypeId = call.parameters["groupTypeId"]?.toInt()
                        val groupObjectId = call.parameters["groupObjectId"]?.toInt()

                        if (groupTypeId == null && groupObjectId == null) {
                            val groups = config.polynomApplicationService.groupsByCatalog(
                                call.userSession.id,
                                IdentifiableObjectTransport(
                                    catalogObjectId!!,
                                    catalogTypeId!!
                                )
                            )
                            call.respond(HttpStatusCode.OK, groups)
                        } else {
                            val groupContent = run {
                                val elementGroups = config.polynomApplicationService.groupsByGroup(
                                    call.userSession.id,
                                    IdentifiableObjectTransport(
                                        groupObjectId!!,
                                        groupTypeId!!
                                    )
                                )
                                val elements = config.polynomApplicationService.elements(
                                    call.userSession.id,
                                    IdentifiableObjectTransport(
                                        groupObjectId,
                                        groupTypeId
                                    )
                                )
                                return@run GroupContent(elementGroups, elements)
                            }
                            call.respond(HttpStatusCode.OK, groupContent)
                        }

                    } catch (e: Exception) {

                    }
                }
            }
            route("elements") {
                get {
                    try {
                        val groupTypeId = call.parameters["groupTypeId"]?.toInt()
                        val groupObjectId = call.parameters["groupObjectId"]?.toInt()

                        val elements = config.polynomApplicationService.elements(
                            call.userSession.id,
                            IdentifiableObjectTransport(
                                groupObjectId!!,
                                groupTypeId!!
                            )
                        )

                    } catch (e: Exception) {

                    }
                }
            }
            route("properties") {
                post {
                    try {
                        val identifier: Identifier = call.receive<Identifier>()
                        val response = config.polynomApplicationService.getProperties(call.userSession.id, Owner(identifier))
                        call.respond(HttpStatusCode.OK, response)
                    } catch (e: Exception) {
                        println("Error fetching properties: ${e.message}")
                    }

                }
            }
        }
    }
}
