package com.khan366kos.integration.studio.ktor.server.app.config

import com.khan366kos.etl.polynom.bff.PolynomApi
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.etl.polynom.bff.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailNotifier
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqPublisher
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncScheduler
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncSchedulerConfig
import com.khan366kos.domain.SessionStore
import com.khan366kos.integration.studio.ktor.server.app.streaming.SyncStreamRegistry
import com.khan366kos.integration.studio.logics.PolynomApplicationService
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
    val syncStreamRegistry: SyncStreamRegistry,
    val rabbitMqPublisher: RabbitMqPublisher,
    val syncScheduler: SyncScheduler,
    val migrationRepository: MigrationRepository,
    val schedulerConfig: SyncSchedulerConfig,
) {

    companion object {
        fun create(
            sessionStore: SessionStore,
            httpClient: HttpClient,
            baseUrl: String,
            rabbitMqConfig: RabbitMqConfig,
            migrationRepository: MigrationRepository,
            emailConfig: EmailConfig,
            schedulerConfig: SyncSchedulerConfig,
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
            val emailNotifier = EmailNotifier(emailConfig)

            val syncStreamRegistry = SyncStreamRegistry(
                scope = backgroundScope,
                repository = migrationRepository,
                publisher = rabbitMqPublisher,
                emailNotifier = emailNotifier,
                externalApiTimezoneOffsetMinutes = schedulerConfig.externalApiTimezoneOffsetMinutes,
            )

            val syncScheduler = SyncScheduler(
                scope = backgroundScope,
                config = schedulerConfig,
                sessionStore = sessionStore,
                polynomApplicationService = polynomApplicationService,
                registry = syncStreamRegistry,
                repository = migrationRepository,
            )

            return AppConfig(
                sessionStore = sessionStore,
                httpClient = httpClient,
                tokenManager = tokenManager,
                authProvider = authProvider,
                polynomApi = polynomApi,
                polynomApplicationService = polynomApplicationService,
                backgroundScope = backgroundScope,
                syncStreamRegistry = syncStreamRegistry,
                rabbitMqPublisher = rabbitMqPublisher,
                syncScheduler = syncScheduler,
                migrationRepository = migrationRepository,
                schedulerConfig = schedulerConfig,
            )
        }
    }
}
