package com.khan366kos.integration.studio.polynom.client

import com.khan366kos.domain.models.business.Element
import com.khan366kos.domain.models.business.elementGroup.ElementGroup
import com.khan366kos.domain.requests.CreateElementRequest
import com.khan366kos.domain.responses.ElementResponse
import com.khan366kos.integration.studio.transport.polynom.response.IPropertyOwnerResponse
import com.khan366kos.etl.mapper.toElement
import com.khan366kos.etl.mapper.toElementGroup
import com.khan366kos.etl.mapper.toDomain
import com.khan366kos.integration.studio.polynom.client.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.polynom.client.auth.TokenManager
import com.khan366kos.integration.studio.transport.polynom.models.LoginRequest
import com.khan366kos.integration.studio.transport.polynom.models.LoginResponse
import com.khan366kos.integration.studio.transport.models.ElementGroupTransport
import com.khan366kos.integration.studio.transport.models.ElementTransport
import com.khan366kos.integration.studio.transport.models.IReference
import com.khan366kos.integration.studio.transport.models.ParentGroup
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.request.IClassificationNodeChildrenRequest
import com.khan366kos.integration.studio.transport.polynom.request.IClassificationTreeRequest
import com.khan366kos.integration.studio.transport.polynom.request.ICreateReferenceRequest
import com.khan366kos.integration.studio.transport.polynom.request.search.IPropertySearchRequest
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import com.khan366kos.integration.studio.transport.polynom.response.AppointedConceptsDto
import com.khan366kos.integration.studio.transport.polynom.response.IClassificationTreeNodeIPaginatedList
import com.khan366kos.integration.studio.transport.polynom.response.search.IPropertySearchResultObjectIPaginatedList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class PolynomApi(
    private val httpClient: HttpClient,
    private val authProvider: SessionStoreAuthProvider,
    baseUrl: String
) {

    private val tokenManager: TokenManager =
        TokenManager(SessionStoreAuthProvider.createTokenRefreshApi(httpClient, baseUrl))

    val conceptApi = ConceptApi(httpClient, tokenManager, authProvider)
    val catalogApi = CatalogApi(httpClient, tokenManager, authProvider)
    val groupApi = GroupApi(httpClient, tokenManager, authProvider)

    suspend fun storageDefinitions(): List<StorageDefinitionTransport> =
        httpClient.get("login/storage-definitions").body()

    suspend fun signIn(loginRequest: LoginRequest): LoginResponse = httpClient.post("login/sign-in") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body()

    suspend fun currentUserInfo(sessionId: String): UserTransport =
        httpClient.get("login/current-user-info") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
        }.body()

    suspend fun references(sessionId: String): List<IReference> =
        httpClient.post("reference/get-all") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
        }.body<List<IReference>>()

    suspend fun reference(sessionId: String, request: IIdentifiableObject): IReference =
        httpClient.post("reference/get-by-id") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    suspend fun referenceCreate(sessionId: String, name: String): IReference =
        httpClient.post("reference/create") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(ICreateReferenceRequest(name = name))
        }.body()

    suspend fun referenceDelete(sessionId: String, request: DeleteReferenceCommand): HttpResponse =
        httpClient.post("reference/delete") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }

    suspend fun groupsByCatalog(sessionId: String, request: IIdentifiableObject): List<ElementGroup> =
        try {
            httpClient.post("element-group/get-by-catalog") {
                authenticate(authProvider.getAuthContext(SessionId(sessionId)))
                setBody(request)
            }.body<List<ElementGroupTransport>>()
                .map {
                    it.toElementGroup()
                }
        } catch (e: Exception) {
            println(e.message)
            throw e
        }

    suspend fun groupsByGroup(sessionId: String, request: IIdentifiableObject): List<ElementGroup> =
        httpClient.post("element-group/get-by-group") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    suspend fun element(sessionId: String, request: CreateElementRequest): ElementResponse =
        httpClient.post("element/create") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    suspend fun elements(sessionId: String, request: IIdentifiableObject): List<Element> =
        httpClient.post("element/get-by-group") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body<List<ElementTransport>>()
            .map { it.toElement() }

    suspend fun getProperties(sessionId: String, request: OwnerRequest): IPropertyOwnerResponse =
        httpClient.post("property-owner/get-properties") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun createElement(sessionId: String, request: ParentGroup): String =
        httpClient.post("element/create") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.bodyAsText()

    suspend fun conceptGetByConceptAppointer(
        sessionId: String,
        request: IIdentifiableObject
    ): AppointedConceptsDto =
        httpClient.post("concept/get-by-concept-appointer") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    suspend fun executePropertySearch(
        sessionId: String,
        request: IPropertySearchRequest
    ): IPropertySearchResultObjectIPaginatedList =
        httpClient.post("search/execute-property-search") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    suspend fun getClassification(
        sessionId: String,
        request: IClassificationTreeRequest
    ): IClassificationTreeNodeIPaginatedList =
        httpClient.post("tree/get-classification") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    suspend fun getClassificationNodeChildren(
        sessionId: String,
        request: IClassificationNodeChildrenRequest
    ): IClassificationTreeNodeIPaginatedList =
        httpClient.post("tree/get-classification-node-children") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }
}
