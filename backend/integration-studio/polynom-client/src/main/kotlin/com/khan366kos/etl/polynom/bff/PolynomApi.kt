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
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class PolynomApi(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) {
    suspend fun storageDefinitions(): List<StorageDefinitionTransport> =
        httpClient.get("login/storage-definitions").body()

    suspend fun signIn(loginRequest: LoginRequest): LoginResponse = httpClient.post("login/sign-in") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body()

    suspend fun currentUserInfo(authContext: AuthContext): UserTransport =
        httpClient.get("login/current-user-info") {
            authenticate(authContext)
        }.body()

    suspend fun references(authContext: AuthContext): List<Reference> {
        return httpClient.post("reference/get-all") {
            authenticate(authContext)
        }.body<List<ReferenceTransport>>()
            .map { it.toReference() }
    }

    suspend fun reference(authContext: AuthContext, request: IdentifiableObjectTransport): Reference =
        httpClient.post("reference/get-by-id") {
            authenticate(authContext)
            setBody(request)
        }.body<ReferenceTransport>()
            .toReference()

    suspend fun referenceCreate(authContext: AuthContext, request: CreateReferenceCommand): CreateReferenceResponse =
        httpClient.post("reference/create") {
            authenticate(authContext)
            setBody(request)
        }.body<ReferenceTransport>()
            .toCreateReferenceResponse()

    suspend fun referenceDelete(authContext: AuthContext, request: DeleteReferenceCommand): HttpResponse =
        httpClient.post("reference/delete") {
            authenticate(authContext)
            setBody(request)
        }

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

    suspend fun groupsByCatalog(authContext: AuthContext, request: IdentifiableObjectTransport): List<ElementGroup> =
        httpClient.post("element-group/get-by-catalog") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    suspend fun groupsByGroup(authContext: AuthContext, request: IdentifiableObjectTransport): List<ElementGroup> =
        httpClient.post("element-group/get-by-group") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    suspend fun element(authContext: AuthContext, request: CreateElementRequest): ElementResponse =
        httpClient.post("element/create") {
            authenticate(authContext)
            setBody(request)
        }.body()

    suspend fun elements(authContext: AuthContext, request: IdentifiableObjectTransport): List<Element> =
        httpClient.post("element/get-by-group") {
            authenticate(authContext)
            setBody(request)
        }.body<List<ElementTransport>>()
            .map { it.toElement() }

    suspend fun getProperties(authContext: AuthContext, request: Owner): PropertyOwnerResponse =
        httpClient.post("property-owner/get-properties") {
            authenticate(authContext)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun setPropertyValues(authContext: AuthContext, request: PropertyAssignmentRequest): String =
        httpClient.put("property-owner/set-property-values") {
            authenticate(authContext)
            setBody(request)
        }.bodyAsText()
    
    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }
}
