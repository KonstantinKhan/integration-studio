package com.khan366kos.integration.studio.ktor.server.app.config

import com.khan366kos.etl.polynom.bff.PolynomApi
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.infrastructure.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.ktor.server.app.session.SessionStore
import io.ktor.client.HttpClient

/**
 * Configuration class for the application.
 * Contains all dependencies needed for the application layers.
 *
 * @param sessionStore хранилище сессий
 * @param httpClient HTTP-клиент для PolynomApi
 * @param tokenManager менеджер токенов
 * @param authProvider провайдер аутентификации
 * @param polynomApi HTTP-адаптер для Polynom API
 * @param polynomApplicationService сервис приложения
 */
class AppConfig(
    val sessionStore: SessionStore,
    val httpClient: HttpClient,
    val tokenManager: TokenManager,
    val authProvider: SessionStoreAuthProvider,
    val polynomApi: PolynomApi,
    val polynomApplicationService: PolynomApplicationService
) {
    companion object {
        /**
         * Creates a new AppConfig with all dependencies wired up.
         *
         * @param sessionStore хранилище сессий
         * @param httpClient HTTP-клиент для PolynomApi
         * @param baseUrl базовый URL Polynom API
         * @return настроенный AppConfig
         */
        fun create(
            sessionStore: SessionStore,
            httpClient: HttpClient,
            baseUrl: String
        ): AppConfig {
            val tokenRefreshApi = SessionStoreAuthProvider.createTokenRefreshApi(httpClient, baseUrl)
            val tokenManager = TokenManager(tokenRefreshApi)
            val authProvider = SessionStoreAuthProvider(sessionStore, tokenManager, httpClient, baseUrl)
            val polynomApi = PolynomApi(httpClient, tokenManager)
            val polynomApplicationService = PolynomApplicationService(authProvider, polynomApi)

            return AppConfig(
                sessionStore = sessionStore,
                httpClient = httpClient,
                tokenManager = tokenManager,
                authProvider = authProvider,
                polynomApi = polynomApi,
                polynomApplicationService = polynomApplicationService
            )
        }
    }
}
