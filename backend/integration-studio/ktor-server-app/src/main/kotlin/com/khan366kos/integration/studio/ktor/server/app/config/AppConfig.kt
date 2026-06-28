package com.khan366kos.integration.studio.ktor.server.app.config

import com.khan366kos.etl.polynom.bff.PolynomApi
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.infrastructure.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqPublisher
import com.khan366kos.integration.studio.ktor.server.app.session.SessionStore
import com.khan366kos.integration.studio.ktor.server.app.streaming.MigrationStreamRegistry
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

class AppConfig(
    val sessionStore: SessionStore,
    val httpClient: HttpClient,
    val tokenManager: TokenManager,
    val authProvider: SessionStoreAuthProvider,
    val polynomApi: PolynomApi,
    val polynomApplicationService: PolynomApplicationService,
    val backgroundScope: CoroutineScope,
    val migrationStreamRegistry: MigrationStreamRegistry,
    val rabbitMqPublisher: RabbitMqPublisher,
) {

    companion object {
        fun create(
            sessionStore: SessionStore,
            httpClient: HttpClient,
            baseUrl: String,
            rabbitMqConfig: RabbitMqConfig,
            migrationRepository: MigrationRepository,
        ): AppConfig {
            val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

            val tokenRefreshApi = SessionStoreAuthProvider.createTokenRefreshApi(httpClient, baseUrl)
            val tokenManager = TokenManager(tokenRefreshApi)
            val authProvider = SessionStoreAuthProvider(sessionStore, tokenManager, httpClient, baseUrl)
            val polynomApi = PolynomApi(httpClient, tokenManager)
            val polynomApplicationService = PolynomApplicationService(authProvider, polynomApi)

            val json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
                classDiscriminator = "type"
            }
            val rabbitMqPublisher = RabbitMqPublisher(rabbitMqConfig, json)

            val migrationStreamRegistry = MigrationStreamRegistry(
                scope = backgroundScope,
                repository = migrationRepository,
                publisher = rabbitMqPublisher,
            )

            return AppConfig(
                sessionStore = sessionStore,
                httpClient = httpClient,
                tokenManager = tokenManager,
                authProvider = authProvider,
                polynomApi = polynomApi,
                polynomApplicationService = polynomApplicationService,
                backgroundScope = backgroundScope,
                migrationStreamRegistry = migrationStreamRegistry,
                rabbitMqPublisher = rabbitMqPublisher,
            )
        }
    }
}
