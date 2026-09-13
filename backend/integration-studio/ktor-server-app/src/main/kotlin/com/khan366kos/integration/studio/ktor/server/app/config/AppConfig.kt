package com.khan366kos.integration.studio.ktor.server.app.config

import com.khan366kos.integration.studio.polynom.client.PolynomClient
import com.khan366kos.integration.studio.polynom.client.auth.TokenManager
import com.khan366kos.integration.studio.polynom.client.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailNotifier
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqPublisher
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncScheduler
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncSchedulerConfig
import com.khan366kos.domain.SessionStore
import com.khan366kos.integration.studio.ktor.server.app.streaming.SyncStreamRegistry
import com.khan366kos.integration.studio.loodsman.client.LoodsmanClient
import com.khan366kos.integration.studio.loodsman.session.InMemoryLoodsmanSessionStore
import com.khan366kos.integration.studio.loodsman.session.LoodsmanSessionStore
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import io.ktor.client.HttpClient
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

class AppConfig(
    val sessionStore: SessionStore,
    val polynomApplicationService: PolynomApplicationService,
    val backgroundScope: CoroutineScope,
    val syncStreamRegistry: SyncStreamRegistry,
    val syncScheduler: SyncScheduler,
    val migrationRepository: MigrationRepository,
    val schedulerConfig: SyncSchedulerConfig,
    val environment: ApplicationEnvironment,
    val loodsmanClient: LoodsmanClient,
    val loodsmanSessionStore: LoodsmanSessionStore,
) {

    companion object {
        fun create(
            sessionStore: SessionStore,
            httpClient: HttpClient,
            baseUrl: String,
            loodsmanHttpClient: HttpClient,
            rabbitMqConfig: RabbitMqConfig,
            migrationRepository: MigrationRepository,
            emailConfig: EmailConfig,
            schedulerConfig: SyncSchedulerConfig,
            environment: ApplicationEnvironment
        ): AppConfig {
            val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            val tokenRefreshApi = SessionStoreAuthProvider.createTokenRefreshApi(httpClient, baseUrl)
            val tokenManager = TokenManager(tokenRefreshApi)
            val authProvider = SessionStoreAuthProvider(sessionStore, tokenManager, httpClient)
            val polynomClient = PolynomClient(httpClient, authProvider, tokenManager)
            val polynomApplicationService = PolynomApplicationService(polynomClient)
            val loodsmanClient = LoodsmanClient(loodsmanHttpClient)
            val loodsmanSessionStore = InMemoryLoodsmanSessionStore()

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
                polynomApplicationService = polynomApplicationService,
                backgroundScope = backgroundScope,
                syncStreamRegistry = syncStreamRegistry,
                syncScheduler = syncScheduler,
                migrationRepository = migrationRepository,
                schedulerConfig = schedulerConfig,
                environment = environment,
                loodsmanClient = loodsmanClient,
                loodsmanSessionStore = loodsmanSessionStore,
            )
        }
    }
}
