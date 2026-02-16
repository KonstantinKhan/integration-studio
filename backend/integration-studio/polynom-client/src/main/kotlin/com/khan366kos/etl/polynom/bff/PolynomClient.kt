package com.khan366kos.etl.polynom.bff

import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.common.models.business.Catalog
import com.khan366kos.common.models.business.Element
import com.khan366kos.integration.studio.transport.models.ReferenceTransport
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.common.models.business.Reference
import com.khan366kos.etl.mapper.toReference
import com.khan366kos.common.models.business.elementGroup.ElementGroup
import com.khan366kos.common.responses.ElementResponse
import com.khan366kos.common.responses.PropertyOwnerRespose
import com.khan366kos.common.requests.CreateElementRequest
import com.khan366kos.common.requests.PropertyAssignmentRequest
import com.khan366kos.common.requests.PropertyOwnerRequest
import com.khan366kos.etl.mapper.toCatalog
import com.khan366kos.etl.mapper.toElement
import com.khan366kos.etl.mapper.toElementGroup
import com.khan366kos.etl.polynom.bff.auth.AuthPlugin
import com.khan366kos.etl.polynom.bff.auth.CredentialsContext
import com.khan366kos.etl.polynom.bff.auth.SessionIdAttrKey
import com.khan366kos.etl.polynom.bff.auth.UserCredentialsAttrKey
import com.khan366kos.integration.studio.transport.models.ElementGroupTransport
import com.khan366kos.integration.studio.transport.models.IdentifiableObjectTransport
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import com.khan366kos.etl.polynom.bff.auth.LoginResponse
import com.khan366kos.integration.studio.transport.models.ElementCatalogTransport
import com.khan366kos.integration.studio.transport.models.ElementTransport
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class PolynomClient {
    companion object {
        private const val BASE_API_PATH = "/api/v1"
        private const val BASE_URL = "https://delusively-altruistic-pangolin.cloudpub.ru:443"
    }

    internal val _credentialsUpdates =
        MutableSharedFlow<Pair<String, UserCredentials>>(replay = 0, extraBufferCapacity = 64)
    val credentialsUpdates = _credentialsUpdates.asSharedFlow()

    private val tokenManager = TokenManager()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "delusively-altruistic-pangolin.cloudpub.ru"
                port = 443
                path("$BASE_API_PATH/")
            }
            contentType(ContentType.Application.Json)
        }
        install(AuthPlugin) {
            baseUrl = "$BASE_URL$BASE_API_PATH"
            tokenManager = this@PolynomClient.tokenManager
            polynomClient = this@PolynomClient
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
        }
    }.also { httpClient ->
        httpClient.requestPipeline.intercept(HttpRequestPipeline.Before) {
            currentCoroutineContext()[CredentialsContext]?.let {
                context.attributes.put(UserCredentialsAttrKey, it.credentials)
                context.attributes.put(SessionIdAttrKey, it.sessionId)
            }
        }
    }

    suspend fun storageDefinitions(): List<StorageDefinitionTransport> =
        client.get("login/storage-definitions").body()

    suspend fun signIn(loginRequest: LoginRequest): LoginResponse = client.post("login/sign-in") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body()

    suspend fun currentUserInfo(): UserTransport =
        client.get("login/current-user-info").body()

    suspend fun references(): List<Reference> {
        return client.post("reference/get-all")
            .body<List<ReferenceTransport>>()
            .map { it.toReference() }
    }

    suspend fun reference(request: IdentifiableObjectTransport): Reference = client.post("reference/get-by-id") {
        setBody(request)
    }.body<ReferenceTransport>()
        .toReference()

    suspend fun catalogs(request: IdentifiableObjectTransport): List<Catalog> =
        client.post("element-catalog/get-by-reference") {
            setBody(request)
        }
            .body<List<ElementCatalogTransport>>()
            .map { it.toCatalog() }

    suspend fun catalog(request: IdentifiableObjectTransport): Catalog = client.post("element-catalog/get-by-id") {
        setBody(request)
    }.body<ElementCatalogTransport>()
        .toCatalog()

    suspend fun groupsByCatalog(request: IdentifiableObjectTransport): List<ElementGroup> =
        client.post("element-group/get-by-catalog") {
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    suspend fun groupsByGroup(request: IdentifiableObjectTransport): List<ElementGroup> =
        client.post("element-group/get-by-group") {
            setBody(request)
        }.body<List<ElementGroupTransport>>()
            .map { it.toElementGroup() }

    suspend fun element(request: CreateElementRequest): ElementResponse {
        return client.post("element/create") {
            setBody(request)
        }.body()
    }

    suspend fun propertyOwner(request: PropertyOwnerRequest): PropertyOwnerRespose {
        return client.post("property-owner/properties") {
            setBody(request.identifier)
        }.body()
    }

    suspend fun setPropertyValues(request: PropertyAssignmentRequest): String {
        return client.put("property-owner/set-property-values") {
            setBody(request)
        }.bodyAsText()
    }

    suspend fun elements(request: IdentifiableObjectTransport): List<Element> {
        return client.post("element/get-by-group") {
            setBody(request)
        }.body<List<ElementTransport>>()
            .map { it.toElement() }
    }

    fun close() {
        client.close()
    }
}