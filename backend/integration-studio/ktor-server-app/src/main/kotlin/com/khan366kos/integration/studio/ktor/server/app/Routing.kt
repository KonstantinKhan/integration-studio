package com.khan366kos.integration.studio.ktor.server.app

import com.khan366kos.domain.models.auth.simple.AccessToken
import com.khan366kos.domain.models.auth.simple.Login
import com.khan366kos.domain.models.auth.simple.RefreshToken
import com.khan366kos.domain.models.auth.simple.StorageId
import com.khan366kos.domain.models.business.GroupContent
import com.khan366kos.integration.studio.transport.models.AuthorizationRequestTransport
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.SessionInterceptorPlugin
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.ktor.server.app.routes.catalogs
import com.khan366kos.integration.studio.ktor.server.app.routes.connections
import com.khan366kos.integration.studio.transport.polynom.models.LoginRequest
import com.khan366kos.integration.studio.ktor.server.app.routes.concept
import com.khan366kos.integration.studio.ktor.server.app.routes.migration
import com.khan366kos.integration.studio.ktor.server.app.routes.propertyOwner
import com.khan366kos.integration.studio.ktor.server.app.routes.references
import com.khan366kos.integration.studio.ktor.server.app.routes.search
import com.khan366kos.integration.studio.ktor.server.app.routes.searchStream
import com.khan366kos.integration.studio.ktor.server.app.routes.syncSummary
import com.khan366kos.integration.studio.ktor.server.app.routes.tree
import com.khan366kos.integration.studio.loodsman.client.LoodsmanUnauthorizedException
import com.khan366kos.integration.studio.loodsman.session.LoodsmanSession
import com.khan366kos.integration.studio.transport.loodsman.models.LoginInputDto
import com.khan366kos.integration.studio.transport.models.ParentGroup
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Serializable
import kotlin.system.measureTimeMillis
import java.util.UUID

@Serializable
data class SessionCheckResponse(
    val authenticated: Boolean,
    val sessionId: String? = null,
    val username: String? = null
)

@Serializable
data class TestConcurrentResponse(
    val count: Int,
    val concurrency: Int,
    val elapsedMs: Long,
    val requestsPerSec: Double,
    val avgMsPerRequest: Double,
    val results: List<String>
)

@Serializable
data class LoodsmanAuthorizeRequest(
    val dbName: String,
    val username: String,
    val password: String
)

@Serializable
data class LoodsmanAuthorizeResponse(
    val authenticated: Boolean,
    val sessionId: String,
    val username: String? = null,
    val dbName: String? = null
)

@Serializable
data class TreeRootItemBffDto(
    val id: Int?,
    val idType: Int?,
    val hasLink: Boolean?,
    val product: String? = null,
    val version: String? = null,
    val idState: Int? = null,
    val idLock: Int? = null,
    val accessLevel: Int? = null,
    val label: Int? = null,
    val labelName: String? = null,
    val idLink: Int? = null,
    val idLinkType: Int? = null,
    val minQuantity: Double? = null,
    val maxQuantity: Double? = null
)

@Serializable
data class LoodsmanObjectPropertiesBffDto(
    val type: String?,
    val product: String?,
    val version: String?,
    val state: String?
)

@Serializable
data class LoodsmanObjectVersionBffDto(
    val idVersion: Int = 0,
    val version: String?,
    val state: String?,
    val dateOfCreate: String?
)

@Serializable
data class LoodsmanAttributeBffDto(
    val name: String?,
    val value: String?
)

@Serializable
data class LoodsmanLinkQuantityBffDto(
    val value: Double?,
    val min: Double?,
    val max: Double?
)

@Serializable
data class LoodsmanLinkBffDto(
    val linkId: Int = 0,
    val product: String?,
    val version: String?,
    val type: String?,
    val quantity: LoodsmanLinkQuantityBffDto,
    val attributes: List<LoodsmanAttributeBffDto>
)

@Serializable
data class LoodsmanObjectInfoBffDto(
    val idVersion: Int = 0,
    val properties: LoodsmanObjectPropertiesBffDto,
    val versions: List<LoodsmanObjectVersionBffDto>,
    val attributes: List<LoodsmanAttributeBffDto>,
    val links: List<LoodsmanLinkBffDto>
)

@OptIn(ExperimentalCoroutinesApi::class)
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
                val credentials = com.khan366kos.domain.models.auth.UserCredentials(
                    login = Login(authRequest.username),
                    storageId = StorageId(authRequest.storageId),
                    accessToken = AccessToken(response.accessToken ?: ""),
                    refreshToken = RefreshToken(response.refreshToken ?: ""),
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

        post("/logout") {
            val session = call.sessions.get<UserSession>()
            if (session != null) {
                val loodsmanSession = config.loodsmanSessionStore.retrieve(session.id)
                config.sessionStore.remove(session.id)
                config.loodsmanSessionStore.remove(session.id)
                if (loodsmanSession != null) {
                    try {
                        config.loodsmanClient.authApi.logout(loodsmanSession.sessionId, loodsmanSession.dbName)
                    } catch (e: Exception) {
                        application.log.warn("Loodsman remote logout failed: ${e.message}")
                    }
                }
            }
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, mapOf("message" to "Вы вышли из системы"))
        }

//        post("/upload") {
//            val multipartData = call.receiveMultipart()
//            var fileName: String? = null
//            var tempFile: File? = null
//
//            try {
//                multipartData.forEachPart { part ->
//                    when (part) {
//                        is PartData.FileItem -> {
//                            fileName = part.originalFileName ?: "uploaded.xlsx"
//                            tempFile = Files.createTempFile("upload_", "_${fileName}").toFile()
//
//                            @Suppress("DEPRECATION")
//                            part.streamProvider().use { input ->
//                                tempFile!!.writeBytes(input.readBytes())
//                            }
//                        }
//
//                        else -> {}
//                    }
//                    part.dispose()
//                }
//
//                if (tempFile == null) {
//                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Файл не найден"))
//                    return@post
//                }
//
//                val result = useManagedWorkbook {
//                    path = tempFile.absolutePath
//                }
//
//                when (result) {
//                    is ManagedWorkbookResult.Success -> {
//                        call.respond(result.etlWorkbook.toEtlWorkbookTransport())
//                    }
//
//                    is ManagedWorkbookResult.Failure -> {
//                        call.respond(
//                            HttpStatusCode.UnprocessableEntity,
//                            mapOf("error" to "Ошибка обработки файла: ${result.exception.message}")
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                call.respond(
//                    HttpStatusCode.InternalServerError,
//                    mapOf("error" to "Ошибка сервера: ${e.message}")
//                )
//            } finally {
//                tempFile?.delete()
//            }
//        }

        get("/") {
            call.respondText("Hello World!")
        }

        route("/") {
            install(SessionInterceptorPlugin) {
                sessionStore = config.sessionStore
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
                                IIdentifiableObject(
                                    catalogObjectId!!,
                                    catalogTypeId!!
                                )
                            )
                            call.respond(HttpStatusCode.OK, groups)
                        } else {
                            val groupContent = run {
                                val elementGroups = config.polynomApplicationService.groupsByGroup(
                                    call.userSession.id,
                                    IIdentifiableObject(
                                        groupObjectId!!,
                                        groupTypeId!!
                                    )
                                )
                                val elements = config.polynomApplicationService.elements(
                                    call.userSession.id,
                                    IIdentifiableObject(
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
                            IIdentifiableObject(
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
                        val identifier = call.receive<IIdentifiableObject>()
                        val response = config.polynomApplicationService.getProperties(
                            call.userSession.id,
                            OwnerRequest(identifier)
                        )
                        call.respond(HttpStatusCode.OK, response)
                    } catch (e: Exception) {
                        println("Error fetching properties: ${e.message}")
                    }

                }
            }
            route("test") {
                get {
                    try {
                        val groupTypeId = call.parameters["groupTypeId"]?.toInt()
                        val groupObjectId = call.parameters["groupObjectId"]?.toInt()
                        val count = call.parameters["count"]?.toInt() ?: 100
                        val concurrency = call.parameters["concurrency"]?.toInt() ?: 10

                        if (groupTypeId == null || groupObjectId == null) {
                            call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to "Missing groupTypeId or groupObjectId")
                            )
                            return@get
                        }

                        val sessionId = call.userSession.id
                        val parentGroup = ParentGroup(IIdentifiableObject(groupObjectId, groupTypeId))

                        val results: List<String>
                        val elapsed = measureTimeMillis {
                            results = (1..count).asFlow()
                                .flatMapMerge(concurrency) {
                                    flow { emit(config.polynomApplicationService.create(sessionId, parentGroup)) }
                                }
                                .toList()
                        }

                        val rps = Math.round(count / (elapsed / 1000.0) * 100.0) / 100.0
                        val avg = Math.round(elapsed.toDouble() / count * 10.0) / 10.0

                        println("$count calls with concurrency=$concurrency completed in ${elapsed}ms ($rps req/s, avg ${avg}ms/req)")
                        call.respond(
                            HttpStatusCode.OK,
                            TestConcurrentResponse(count, concurrency, elapsed, rps, avg, results)
                        )
                    } catch (e: Exception) {
                        application.log.error("Error in /test: ${e.message}", e)
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to "Ошибка: ${e.message}")
                        )
                    }
                }
            }
            concept(config)
            propertyOwner(config)
            search(config)
            searchStream(config)
            syncSummary(config.migrationRepository, config.schedulerConfig)
            references(config)
            tree(config)
            catalogs(config)
            migration(config, environment.config.property("excel.path").getString())
            connections(config)
            loodsman(config)
        }
    }
}

fun Route.loodsman(config: AppConfig): Route = route("loodsman") {
    get("/databases") {
        try {
            val databases = config.loodsmanClient.authApi.databases()
            call.respond(HttpStatusCode.OK, databases.map { mapOf("name" to it.name) })
        } catch (e: Exception) {
            call.application.log.error("Error fetching loodsman databases: ${e.message}", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Ошибка получения списка баз данных: ${e.message}")
            )
        }
    }

    post("/authorize") {
        try {
            val authRequest = call.receive<LoodsmanAuthorizeRequest>()

            val response = config.loodsmanClient.authApi.login(
                LoginInputDto(
                    dbName = authRequest.dbName,
                    username = authRequest.username,
                    password = authRequest.password,
                    rememberMe = null
                )
            )

            val remoteSessionId = response.sessionId
            if (remoteSessionId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Ошибка авторизации в Loodsman: сессия не создана")
                )
                return@post
            }

            val bffSessionId = UUID.randomUUID().toString()
            val dbName = response.dbName ?: authRequest.dbName
            config.loodsmanSessionStore.store(
                bffSessionId,
                LoodsmanSession(
                    sessionId = remoteSessionId,
                    dbName = dbName,
                    userId = response.userId,
                    username = authRequest.username
                )
            )
            call.sessions.set(UserSession(id = bffSessionId, username = authRequest.username))

            call.respond(
                HttpStatusCode.OK,
                LoodsmanAuthorizeResponse(
                    authenticated = true,
                    sessionId = bffSessionId,
                    username = authRequest.username,
                    dbName = dbName
                )
            )
        } catch (e: Exception) {
            call.application.log.error("Loodsman authorization error: ${e.message}", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Ошибка авторизации: ${e.message}")
            )
        }
    }

    get("/check-session") {
        val session = call.sessions.get<UserSession>()
        val loodsmanSession = session?.let { config.loodsmanSessionStore.retrieve(it.id) }
        if (session != null && loodsmanSession != null) {
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

    post("/logout") {
        val session = call.sessions.get<UserSession>()
        if (session != null) {
            val loodsmanSession = config.loodsmanSessionStore.retrieve(session.id)
            if (loodsmanSession != null) {
                try {
                    config.loodsmanClient.authApi.logout(loodsmanSession.sessionId, loodsmanSession.dbName)
                } catch (e: Exception) {
                    call.application.log.warn("Loodsman remote logout failed: ${e.message}")
                }
            }
            config.loodsmanSessionStore.remove(session.id)
        }
        call.sessions.clear<UserSession>()
        call.respond(HttpStatusCode.OK, mapOf("message" to "Вы вышли из системы"))
    }

    get("/tree/root") {
        val session = call.sessions.get<UserSession>()
        val loodsmanSession = session?.let { config.loodsmanSessionStore.retrieve(it.id) }
        if (loodsmanSession == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Сессия не найдена"))
            return@get
        }

        try {
            val tree = config.loodsmanClient.pdmApi.getTree(
                sessionId = loodsmanSession.sessionId,
                dbName = loodsmanSession.dbName
            )
            call.respond(
                HttpStatusCode.OK,
                tree.map { node ->
                    TreeRootItemBffDto(
                        id = node.id,
                        idType = node.idType,
                        hasLink = node.hasLink,
                        product = node.product,
                        version = node.version,
                        idState = node.idState,
                        idLock = node.idLock,
                        accessLevel = node.accessLevel,
                        label = node.label,
                        labelName = node.labelName,
                        idLink = node.idLink,
                        idLinkType = node.idLinkType,
                        minQuantity = node.minQuantity,
                        maxQuantity = node.maxQuantity
                    )
                }
            )
        } catch (e: LoodsmanUnauthorizedException) {
            session?.let { config.loodsmanSessionStore.remove(it.id) }
            call.application.log.warn("Loodsman session rejected for tree fetch: ${e.message}")
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf("error" to "Ошибка получения дерева: сессия Loodsman недействительна")
            )
        } catch (e: Exception) {
            call.application.log.error("Error fetching loodsman tree: ${e.message}", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Ошибка получения дерева: ${e.message}")
            )
        }
    }

    get("/object-info") {
        val session = call.sessions.get<UserSession>()
        val loodsmanSession = session?.let { config.loodsmanSessionStore.retrieve(it.id) }
        if (loodsmanSession == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Сессия не найдена"))
            return@get
        }

        val idVersion = call.parameters["idVersion"]?.toIntOrNull()
        if (idVersion == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Некорректный параметр idVersion: ожидается целое число")
            )
            return@get
        }

        try {
            val propObjects = config.loodsmanClient.objectInfoApi.getPropObjects(
                sessionId = loodsmanSession.sessionId,
                dbName = loodsmanSession.dbName,
                objectList = idVersion.toString()
            )
            val prop = propObjects.firstOrNull()
            if (prop == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Объект с идентификатором ${idVersion} не найден")
                )
                return@get
            }
            val versions = if (prop.type != null || prop.product != null) {
                config.loodsmanClient.objectInfoApi.getVersionList(
                    sessionId = loodsmanSession.sessionId,
                    dbName = loodsmanSession.dbName,
                    typeName = prop.type,
                    productName = prop.product
                )
            } else {
                emptyList()
            }
            val attributes = config.loodsmanClient.objectInfoApi.getInfoAboutVersionMode3(
                sessionId = loodsmanSession.sessionId,
                dbName = loodsmanSession.dbName,
                idVersion = idVersion
            )
            val lObjs = config.loodsmanClient.objectInfoApi.getLObjs(
                sessionId = loodsmanSession.sessionId,
                dbName = loodsmanSession.dbName,
                versionId = idVersion,
                inverse = false
            )
            val typeByVersionId = if (lObjs.isNotEmpty()) {
                val versionIds = lObjs.map { it.versionId }.distinct()
                coroutineScope {
                    versionIds.chunked(100).map { chunk ->
                        async {
                            config.loodsmanClient.objectInfoApi.getPropObjects(
                                sessionId = loodsmanSession.sessionId,
                                dbName = loodsmanSession.dbName,
                                objectList = chunk.joinToString(",")
                            ).associate { it.idVersion to it.type }
                        }
                    }.awaitAll()
                }.reduce { acc, map -> acc + map }
            } else {
                emptyMap()
            }
            val links = coroutineScope {
                lObjs.map { linkedObject ->
                    async {
                        val linkAttributes = config.loodsmanClient.objectInfoApi.getLinkAttributes(
                            sessionId = loodsmanSession.sessionId,
                            dbName = loodsmanSession.dbName,
                            linkId = linkedObject.linkId
                        )
                        val quantity = if (linkedObject.minQuantity != null && linkedObject.minQuantity == linkedObject.maxQuantity) {
                            LoodsmanLinkQuantityBffDto(value = linkedObject.minQuantity, min = null, max = null)
                        } else {
                            LoodsmanLinkQuantityBffDto(value = null, min = linkedObject.minQuantity, max = linkedObject.maxQuantity)
                        }
                        LoodsmanLinkBffDto(
                            linkId = linkedObject.linkId,
                            product = linkedObject.product,
                            version = linkedObject.version,
                            type = typeByVersionId[linkedObject.versionId],
                            quantity = quantity,
                            attributes = linkAttributes.map { LoodsmanAttributeBffDto(name = it.name, value = it.value) }
                        )
                    }
                }.awaitAll()
            }

            call.respond(
                HttpStatusCode.OK,
                LoodsmanObjectInfoBffDto(
                    idVersion = idVersion,
                    properties = LoodsmanObjectPropertiesBffDto(
                        type = prop.type,
                        product = prop.product,
                        version = prop.version,
                        state = prop.state
                    ),
                    versions = versions.map {
                        LoodsmanObjectVersionBffDto(
                            idVersion = it.idVersion,
                            version = it.version,
                            state = it.state,
                            dateOfCreate = it.dateOfCreate
                        )
                    },
                    attributes = attributes.map { LoodsmanAttributeBffDto(name = it.name, value = it.value) },
                    links = links
                )
            )
        } catch (e: LoodsmanUnauthorizedException) {
            session?.let { config.loodsmanSessionStore.remove(it.id) }
            call.application.log.warn("Loodsman session rejected for object info fetch: ${e.message}")
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf("error" to "Ошибка получения информации об объекте: сессия Loodsman недействительна")
            )
        } catch (e: Exception) {
            call.application.log.error("Error fetching loodsman object info: ${e.message}", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Ошибка получения информации об объекте: ${e.message}")
            )
        }
    }
}
