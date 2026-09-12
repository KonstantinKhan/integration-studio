package com.khan366kos.integration.studio.polynom.client

import com.khan366kos.integration.studio.polynom.client.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.polynom.client.auth.TokenManager
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.models.login.IStorageDefinition
import com.khan366kos.integration.studio.transport.polynom.models.LoginRequest
import com.khan366kos.integration.studio.transport.polynom.models.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class LoginApi(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager,
    private val authProvider: SessionStoreAuthProvider
) {

    suspend fun storageDefinitions(): List<IStorageDefinition> =
        httpClient.get("login/storage-definitions").body()

    suspend fun signIn(loginRequest: LoginRequest): LoginResponse = httpClient.post("login/sign-in") {
        contentType(ContentType.Application.Json)
        setBody(loginRequest)
    }.body()

    suspend fun currentUserInfo(sessionId: String): UserTransport =
        httpClient.get("login/current-user-info") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
        }.body()

    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }
}
