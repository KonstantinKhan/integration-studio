package com.khan366kos.integration.studio.polynom.client.auth

import com.khan366kos.domain.SessionStore
import com.khan366kos.integration.studio.polynom.client.AuthContext
import com.khan366kos.domain.models.auth.AuthenticationException
import com.khan366kos.integration.studio.polynom.client.SessionId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

class SessionStoreAuthProvider(
    private val sessionStore: SessionStore,
    private val tokenManager: TokenManager,
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthProvider {

    override suspend fun getAuthContext(sessionId: SessionId): AuthContext {
        val credentials = sessionStore.retrieve(sessionId.value)
            ?: throw AuthenticationException.SessionNotFound(sessionId.value)

        return AuthContext(sessionId, credentials)
    }

    override suspend fun refreshAuth(sessionId: SessionId): AuthContext {
        val currentCredentials = getAuthContext(sessionId).credentials

        val refreshedCredentials = tokenManager.authenticate(
            sessionId = sessionId.value,
            userCredentials = currentCredentials
        )

        sessionStore.updateCredentials(sessionId.value, refreshedCredentials)

        return AuthContext(sessionId, refreshedCredentials)
    }

    companion object {
        fun createTokenRefreshApi(
            httpClient: HttpClient,
            baseUrl: String
        ): TokenRefreshApi = TokenRefreshApi { accessToken, refreshToken ->
            val response = httpClient.patch("$baseUrl/login/update-token") {
                header(HttpHeaders.Authorization, "Bearer ${accessToken.value}")
                header(HttpHeaders.ContentType, "text/plain")
                setBody(refreshToken.value)
            }
            response.body()
        }
    }
}