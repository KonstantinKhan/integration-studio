package com.khan366kos.integration.studio.ktor.server.app.config

import com.khan366kos.integration.studio.ktor.server.app.HttpTimingLogger
import com.khan366kos.integration.studio.ktor.server.app.connection.DatabaseManager
import com.khan366kos.integration.studio.ktor.server.app.connection.RabbitManager
import com.khan366kos.integration.studio.ktor.server.app.connection.ReconnectLoop
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailNotifier
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqPublisher
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncScheduler
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncSchedulerConfig
import com.khan366kos.integration.studio.ktor.server.app.streaming.SyncStreamRegistry
import com.khan366kos.domain.SessionStore
import com.khan366kos.integration.studio.loodsman.client.LoodsmanClient
import com.khan366kos.integration.studio.loodsman.session.InMemoryLoodsmanSessionStore
import com.khan366kos.integration.studio.loodsman.session.LoodsmanSessionStore
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import com.khan366kos.integration.studio.polynom.client.PolynomClient
import com.khan366kos.integration.studio.polynom.client.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.polynom.client.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.atomic.AtomicReference

class AppConfig(
    val sessionStore: SessionStore,
    val backgroundScope: CoroutineScope,
    val syncStreamRegistry: SyncStreamRegistry,
    val syncScheduler: SyncScheduler,
    val migrationRepository: MigrationRepository,
    val schedulerConfig: SyncSchedulerConfig,
    val environment: ApplicationEnvironment,
    val loodsmanSessionStore: LoodsmanSessionStore,
    val appSettingsStore: AppSettingsStore,
    val databaseManager: DatabaseManager,
    val rabbitManager: RabbitManager,
    val emailNotifier: EmailNotifier,
    val dbReconnectLoop: ReconnectLoop,
    val rabbitReconnectLoop: ReconnectLoop,
    private val polynomServiceRef: AtomicReference<PolynomApplicationService>,
    private val loodsmanClientRef: AtomicReference<LoodsmanClient>,
    private val polynomHttpClientRef: AtomicReference<HttpClient>,
    private val loodsmanHttpClientRef: AtomicReference<HttpClient>,
) {

    /** Always returns the current service, even after base-url was changed via UI. */
    val polynomApplicationService: PolynomApplicationService
        get() = polynomServiceRef.get()

    /** Always returns the current client, even after base-url was changed via UI. */
    val loodsmanClient: LoodsmanClient
        get() = loodsmanClientRef.get()

    /** Rebuilds the Polynom HTTP client chain with a new base URL (no network calls). */
    fun rebuildPolynomClient(settings: PolynomSettings) {
        val parsed = parseBaseUrl(settings.baseUrl, "polynom.base-url")
        val client = buildHttpClient(parsed)
        val tokenRefreshApi = SessionStoreAuthProvider.createTokenRefreshApi(client, settings.baseUrl)
        val tokenManager = TokenManager(tokenRefreshApi)
        val authProvider = SessionStoreAuthProvider(sessionStore, tokenManager, client)
        val service = PolynomApplicationService(PolynomClient(client, authProvider, tokenManager))
        polynomHttpClientRef.getAndSet(client)?.close()
        polynomServiceRef.set(service)
    }

    /** Rebuilds the Loodsman HTTP client with a new base URL (no network calls). */
    fun rebuildLoodsmanClient(settings: LoodsmanSettings) {
        val parsed = parseBaseUrl(settings.baseUrl, "loodsman.base-url")
        val client = buildHttpClient(parsed)
        loodsmanHttpClientRef.getAndSet(client)?.close()
        loodsmanClientRef.set(LoodsmanClient(client))
    }

    /** Triggers background reconnects for Postgres and RabbitMQ (e.g. after settings change). */
    fun requestServicesReconnect() {
        dbReconnectLoop.requestReconnect()
        rabbitReconnectLoop.requestReconnect()
    }

    companion object {
        private val log = LoggerFactory.getLogger(AppConfig::class.java)

        fun create(
            sessionStore: SessionStore,
            appSettingsStore: AppSettingsStore,
            databaseManager: DatabaseManager,
            rabbitManager: RabbitManager,
            environment: ApplicationEnvironment,
        ): AppConfig {
            val settings = appSettingsStore.currentSettings

            val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            val migrationRepository = MigrationRepository(json = jsonForDb(), databaseManager = databaseManager)

            // Polynom chain.
            val polynomParsed = parseBaseUrl(settings.polynom.baseUrl, "polynom.base-url")
            val polynomHttpClient = buildHttpClient(polynomParsed)
            val tokenRefreshApi = SessionStoreAuthProvider.createTokenRefreshApi(polynomHttpClient, settings.polynom.baseUrl)
            val tokenManager = TokenManager(tokenRefreshApi)
            val authProvider = SessionStoreAuthProvider(sessionStore, tokenManager, polynomHttpClient)
            val polynomService = PolynomApplicationService(
                PolynomClient(polynomHttpClient, authProvider, tokenManager)
            )

            // Loodsman chain.
            val loodsmanParsed = parseBaseUrl(settings.loodsman.baseUrl, "loodsman.base-url")
            val loodsmanHttpClient = buildHttpClient(loodsmanParsed)
            val loodsmanClient = LoodsmanClient(loodsmanHttpClient)

            val rabbitMqPublisher = RabbitMqPublisher(rabbitManager, jsonForDb())
            val emailNotifier = EmailNotifier(settings.email)

            val dbReconnectLoop = ReconnectLoop(backgroundScope, "postgres", databaseManager.status) {
                databaseManager.connect(appSettingsStore.currentSettings.database)
            }
            val rabbitReconnectLoop = ReconnectLoop(backgroundScope, "rabbitmq", rabbitManager.status) {
                rabbitManager.connect(appSettingsStore.currentSettings.rabbitmq)
            }
            dbReconnectLoop.start()
            rabbitReconnectLoop.start()

            val syncStreamRegistry = SyncStreamRegistry(
                scope = backgroundScope,
                repository = migrationRepository,
                publisher = rabbitMqPublisher,
                emailNotifier = emailNotifier,
                externalApiTimezoneOffsetMinutes = settings.scheduler.externalApiTimezoneOffsetMinutes,
            )
            val syncScheduler = SyncScheduler(
                scope = backgroundScope,
                config = settings.scheduler,
                sessionStore = sessionStore,
                polynomApplicationService = polynomService,
                registry = syncStreamRegistry,
                repository = migrationRepository,
            )

            return AppConfig(
                sessionStore = sessionStore,
                backgroundScope = backgroundScope,
                syncStreamRegistry = syncStreamRegistry,
                syncScheduler = syncScheduler,
                migrationRepository = migrationRepository,
                schedulerConfig = settings.scheduler,
                environment = environment,
                loodsmanSessionStore = InMemoryLoodsmanSessionStore(),
                appSettingsStore = appSettingsStore,
                databaseManager = databaseManager,
                rabbitManager = rabbitManager,
                emailNotifier = emailNotifier,
                dbReconnectLoop = dbReconnectLoop,
                rabbitReconnectLoop = rabbitReconnectLoop,
                polynomServiceRef = AtomicReference(polynomService),
                loodsmanClientRef = AtomicReference(loodsmanClient),
                polynomHttpClientRef = AtomicReference(polynomHttpClient),
                loodsmanHttpClientRef = AtomicReference(loodsmanHttpClient),
            )
        }

        private fun jsonForDb(): Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
            classDiscriminator = "type"
        }

        private data class ParsedBaseUrl(
            val scheme: String,
            val host: String,
            val port: Int,
            val basePath: String,
        )

        private fun parseBaseUrl(raw: String, settingName: String): ParsedBaseUrl {
            val uri = URI(raw)
            require(uri.scheme in setOf("http", "https")) {
                "$settingName must use http or https scheme, got: $raw"
            }
            require(!uri.host.isNullOrBlank()) {
                "$settingName must contain a host, got: $raw"
            }
            val port = when {
                uri.port > 0 -> uri.port
                uri.scheme == "https" -> 443
                else -> 80
            }
            return ParsedBaseUrl(uri.scheme, uri.host, port, uri.path.trimEnd('/') + "/")
        }

        private fun buildHttpClient(parsed: ParsedBaseUrl): HttpClient = HttpClient(CIO) {
            engine {
                maxConnectionsCount = 20
                endpoint {
                    connectTimeout = 30_000
                    socketTimeout = 60_000
                    keepAliveTime = 60_000
                }
                requestTimeout = 60_000
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    coerceInputValues = true
                })
            }
            install(HttpTimingLogger)
            defaultRequest {
                contentType(ContentType.Application.Json)
                url {
                    protocol = if (parsed.scheme == "https") URLProtocol.HTTPS else URLProtocol.HTTP
                    host = parsed.host
                    port = parsed.port
                    path(parsed.basePath)
                }
            }
        }
    }
}
