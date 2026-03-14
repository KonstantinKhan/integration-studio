package com.khan366kos.integration.studio.infrastructure.auth

import com.khan366kos.common.models.auth.AuthContext
import com.khan366kos.common.models.auth.AuthenticationException
import com.khan366kos.common.models.auth.SessionId
import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.etl.polynom.bff.auth.AuthProvider
import com.khan366kos.etl.polynom.bff.auth.LoginResponse
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.etl.polynom.bff.auth.TokenRefreshApi
import com.khan366kos.integration.studio.ktor.server.app.session.SessionStore
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

/**
 * Реализация [AuthProvider], использующая [SessionStore] для хранения сессий.
 * 
 * Этот класс связывает domain-слой (AuthProvider) с инфраструктурой (SessionStore),
 * обеспечивая получение и обновление учётных данных пользователя.
 * 
 * @param sessionStore хранилище сессий для получения/сохранения credentials
 * @param tokenManager менеджер токенов для обновления access/refresh токенов
 * @param httpClient HTTP-клиент для вызова API обновления токенов
 * @param baseUrl базовый URL Polynom API для refresh endpoint
 */
class SessionStoreAuthProvider(
    private val sessionStore: SessionStore,
    private val tokenManager: TokenManager,
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthProvider {
    
    /**
     * Получает контекст аутентификации из SessionStore.
     * 
     * @param sessionId идентификатор сессии
     * @return AuthContext с credentials из хранилища
     * @throws AuthenticationException.SessionNotFound если сессия не найдена
     */
    override suspend fun getAuthContext(sessionId: SessionId): AuthContext {
        val credentials = sessionStore.retrieve(sessionId.value)
            ?: throw AuthenticationException.SessionNotFound(sessionId.value)
        
        return AuthContext(sessionId, credentials)
    }
    
    /**
     * Обновляет учётные данные сессии через TokenManager.
     * 
     * @param sessionId идентификатор сессии
     * @return обновлённый AuthContext
     * @throws AuthenticationException.SessionNotFound если сессия не найдена
     * @throws AuthenticationException.TokenExpired если refresh token истёк
     */
    override suspend fun refreshAuth(sessionId: SessionId): AuthContext {
        val currentCredentials = getAuthContext(sessionId).credentials
        
        val refreshedCredentials = tokenManager.authenticate(
            sessionId = sessionId.value,
            userCredentials = currentCredentials
        )
        
        // Сохраняем обновлённые credentials обратно в SessionStore
        sessionStore.updateCredentials(sessionId.value, refreshedCredentials)
        
        return AuthContext(sessionId, refreshedCredentials)
    }
    
    companion object {
        /**
         * Создаёт TokenRefreshApi реализацию для использования с Ktor HttpClient.
         * 
         * @param httpClient HTTP-клиент для вызова refresh endpoint
         * @param baseUrl базовый URL API (например, "https://example.com/api/v1")
         * @return TokenRefreshApi для обновления токенов
         */
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
