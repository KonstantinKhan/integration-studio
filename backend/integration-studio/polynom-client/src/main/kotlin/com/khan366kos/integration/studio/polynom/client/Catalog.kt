package com.khan366kos.integration.studio.polynom.client

import com.khan366kos.integration.studio.polynom.client.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.polynom.client.auth.TokenManager
import com.khan366kos.integration.studio.transport.polynom.models.catalog.IElementCatalog
import com.khan366kos.integration.studio.transport.polynom.request.IIdentifierRequest
import com.khan366kos.integration.studio.transport.polynom.request.catalog.IGetByIdRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

class Catalog(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager,
    private val authProvider: SessionStoreAuthProvider
) {
    suspend fun getById(sessionId: String, typeId: Int, objectId: Int): IElementCatalog =
        httpClient.post("element-catalog/get-by-id") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(IGetByIdRequest(objectId, typeId))
        }.body<IElementCatalog>()

    suspend fun getByReference(sessionId: String, typeId: Int, objectId: Int): List<IElementCatalog> =
        httpClient.post("element-catalog/get-by-reference") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(IIdentifierRequest(objectId, typeId))
        }.body()

    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }
}