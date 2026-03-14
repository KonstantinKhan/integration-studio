package com.khan366kos.etl.polynom.bff

import com.khan366kos.common.models.auth.AuthContext
import com.khan366kos.common.models.business.Catalog
import com.khan366kos.common.models.business.Element
import com.khan366kos.common.models.business.Owner
import com.khan366kos.common.models.business.Reference
import com.khan366kos.common.models.business.elementGroup.ElementGroup
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.common.requests.CreateElementRequest
import com.khan366kos.common.requests.PropertyAssignmentRequest
import com.khan366kos.common.responses.ElementResponse
import com.khan366kos.common.responses.PropertyOwnerResponse
import com.khan366kos.etl.mapper.toCatalog
import com.khan366kos.etl.mapper.toCreateReferenceResponse
import com.khan366kos.etl.mapper.toElement
import com.khan366kos.etl.mapper.toElementGroup
import com.khan366kos.etl.mapper.toReference
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import com.khan366kos.etl.polynom.bff.auth.LoginResponse
import com.khan366kos.integration.studio.transport.models.ElementCatalogTransport
import com.khan366kos.integration.studio.transport.models.ElementGroupTransport
import com.khan366kos.integration.studio.transport.models.ElementTransport
import com.khan366kos.integration.studio.transport.models.ReferenceTransport
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.IdentifiableObjectTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

/**
 * HTTP-адаптер для взаимодействия с Polynom API.
 *
 * Этот класс отвечает только за HTTP-коммуникацию и маппинг данных.
 * Не зависит от Ktor coroutine context — все зависимости явные.
 *
 * @param httpClient HTTP-клиент для вызовов API (с настроенным defaultRequest)
 * @param tokenManager менеджер токенов для обновления credentials при необходимости
 */
class PolynomApi(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) {
    
    // ==================== Authentication ====================

    /**
     * Получает список определений хранилищ (не требует аутентификации).
     */
    suspend fun storageDefinitions(): List<StorageDefinitionTransport> =
        httpClient.get("login/storage-definitions").body()

    /**
     * Выполняет вход пользователя (не требует аутентификации).
     */
    suspend fun signIn(loginRequest: LoginRequest): LoginResponse = httpClient.post("login/sign-in") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body()

    /**
     * Получает информацию о текущем пользователе.
     */
    suspend fun currentUserInfo(authContext: AuthContext): UserTransport =
        httpClient.get("login/current-user-info") {
            authenticate(authContext)
        }.body()
    
    // ==================== References ====================

    /**
     * Получает все справочники.
     */
    suspend fun references(authContext: AuthContext): List<Reference> {
        return httpClient.post("reference/get-all") {
            authenticate(authContext)
        }.body<List<ReferenceTransport>>()
            .map { it.toReference() }
    }

    /**
     * Получает справочник по идентификатору.
     */
    suspend fun reference(authContext: AuthContext, request: IdentifiableObjectTransport): Reference =
        httpClient.post("reference/get-by-id") {
            authenticate(authContext)
            setBody(request)
        }.body<ReferenceTransport>()
            .toReference()

    /**
     * Создаёт новый справочник.
     */
    suspend fun referenceCreate(authContext: AuthContext, request: CreateReferenceCommand): CreateReferenceResponse =
        httpClient.post("reference/create") {
            authenticate(authContext)
            setBody(request)
        }.body<ReferenceTransport>()
            .toCreateReferenceResponse()

    // ==================== Catalogs ====================

    /**
     * Получает каталоги по справочнику.
     */
    suspend fun catalogs(authContext: AuthContext, request: IdentifiableObjectTransport): List<Catalog> =
        httpClient.post("element-catalog/get-by-reference") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementCatalogTransport>>()
            .map { it.toCatalog() }

    /**
     * Получает каталог по идентификатору.
     */
    suspend fun catalog(authContext: AuthContext, request: IdentifiableObjectTransport): Catalog =
        httpClient.post("element-catalog/get-by-id") {
            authenticate(authContext)
            setBody(request)
        }.body<ElementCatalogTransport>()
            .toCatalog()

    // ==================== Groups ====================

    /**
     * Получает группы элементов по каталогу.
     */
    suspend fun groupsByCatalog(authContext: AuthContext, request: IdentifiableObjectTransport): List<ElementGroup> =
        httpClient.post("element-group/get-by-catalog") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    /**
     * Получает группы элементов по группе.
     */
    suspend fun groupsByGroup(authContext: AuthContext, request: IdentifiableObjectTransport): List<ElementGroup> =
        httpClient.post("element-group/get-by-group") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    // ==================== Elements ====================

    /**
     * Создаёт новый элемент.
     */
    suspend fun element(authContext: AuthContext, request: CreateElementRequest): ElementResponse =
        httpClient.post("element/create") {
            authenticate(authContext)
            setBody(request)
        }.body()

    /**
     * Получает элементы по группе.
     */
    suspend fun elements(authContext: AuthContext, request: IdentifiableObjectTransport): List<Element> =
        httpClient.post("element/get-by-group") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementTransport>>()
            .map { it.toElement() }

    // ==================== Properties ====================

    /**
     * Получает свойства владельца.
     */
    suspend fun getProperties(authContext: AuthContext, request: Owner): PropertyOwnerResponse =
        httpClient.post("property-owner/get-properties") {
            authenticate(authContext)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /**
     * Устанавливает значения свойств владельца.
     */
    suspend fun setPropertyValues(authContext: AuthContext, request: PropertyAssignmentRequest): String =
        httpClient.put("property-owner/set-property-values") {
            authenticate(authContext)
            setBody(request)
        }.bodyAsText()
    
    // ==================== Helpers ====================
    
    /**
     * Добавляет заголовок Authorization с валидным токеном.
     * Автоматически обновляет токен при необходимости.
     */
    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }
}
