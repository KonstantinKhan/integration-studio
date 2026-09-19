package com.khan366kos.integration.studio.ktor.server.app

import com.khan366kos.domain.exceptions.RootNodeException
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.config.AppSettings
import com.khan366kos.integration.studio.ktor.server.app.config.AppSettingsStore
import com.khan366kos.integration.studio.ktor.server.app.config.DatabaseSettings
import com.khan366kos.integration.studio.ktor.server.app.config.LoodsmanSettings
import com.khan366kos.integration.studio.ktor.server.app.config.PolynomSettings
import com.khan366kos.integration.studio.ktor.server.app.connection.DatabaseManager
import com.khan366kos.integration.studio.ktor.server.app.connection.RabbitManager
import com.khan366kos.integration.studio.ktor.server.app.errors.ServiceUnavailableException
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqConfig
import com.khan366kos.integration.studio.ktor.server.app.routes.devSessionRoute
import com.khan366kos.integration.studio.ktor.server.app.routes.connections
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncSchedulerConfig
import com.khan366kos.integration.studio.ktor.server.app.session.InMemorySessionStore
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.sessions.*
import io.ktor.server.sse.SSE
import io.ktor.util.AttributeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration.Companion.milliseconds

fun main(args: Array<String>) {
    EngineMain.main(args)
}

private val RequestStartKey = AttributeKey<Long>("RequestStartNs")

val HttpTimingLogger = createClientPlugin("HttpTimingLogger") {
    val callCounter = AtomicLong(0)
    onRequest { request, _ ->
        request.attributes.put(RequestStartKey, System.nanoTime())
    }
    onResponse { call ->
        val start = call.request.attributes[RequestStartKey]
        val ms = (System.nanoTime() - start) / 1_000_000
        val n = callCounter.incrementAndGet()
        println("[HTTP] #$n ${call.request.method.value} ${call.request.url.encodedPath} -> ${call.status.value} ${ms}ms")
    }
}

fun Application.module() {
    val log = LoggerFactory.getLogger("Application")

    // 1. Seed settings from application.conf / env, then overlay settings
    //    previously saved from the UI (local JSON file). Application startup
    //    never depends on any external service being reachable.
    val seedSettings = AppSettings(
        database = DatabaseSettings(
            url      = environment.config.property("database.url").getString(),
            user     = environment.config.property("database.user").getString(),
            password = environment.config.property("database.password").getString(),
            poolSize = environment.config.propertyOrNull("database.pool-size")?.getString()?.toInt() ?: 10,
        ),
        rabbitmq = RabbitMqConfig(
            host        = environment.config.property("rabbitmq.host").getString(),
            port        = environment.config.property("rabbitmq.port").getString().toInt(),
            vhost       = environment.config.property("rabbitmq.vhost").getString(),
            user        = environment.config.property("rabbitmq.user").getString(),
            password    = environment.config.property("rabbitmq.password").getString(),
            exchange    = environment.config.property("rabbitmq.exchange").getString(),
            routingKey  = environment.config.property("rabbitmq.routing-key").getString(),
        ),
        email = EmailConfig(
            enabled  = environment.config.propertyOrNull("email.enabled")?.getString()?.toBoolean() ?: false,
            smtpHost = environment.config.propertyOrNull("email.smtp-host")?.getString() ?: "",
            smtpPort = environment.config.propertyOrNull("email.smtp-port")?.getString()?.toInt() ?: 587,
            smtpTls  = environment.config.propertyOrNull("email.smtp-tls")?.getString()?.toBoolean() ?: true,
            from     = environment.config.propertyOrNull("email.from")?.getString() ?: "",
            password = environment.config.propertyOrNull("email.password")?.getString() ?: "",
            to       = environment.config.propertyOrNull("email.to")?.getString() ?: "",
        ),
        scheduler = SyncSchedulerConfig(
            enabled                         = environment.config.propertyOrNull("sync-scheduler.enabled")?.getString()?.toBoolean() ?: false,
            intervalMinutes                 = environment.config.propertyOrNull("sync-scheduler.interval-minutes")?.getString()?.toLong() ?: 15L,
            scopeTypeId                     = environment.config.propertyOrNull("sync-scheduler.scope-type-id")?.getString()?.toInt() ?: 0,
            scopeObjectId                   = environment.config.propertyOrNull("sync-scheduler.scope-object-id")?.getString()?.toInt() ?: 0,
            serviceUser                     = environment.config.propertyOrNull("sync-scheduler.service-user")?.getString() ?: "",
            servicePassword                 = environment.config.propertyOrNull("sync-scheduler.service-password")?.getString() ?: "",
            serviceStorageId                = environment.config.propertyOrNull("sync-scheduler.service-storage-id")?.getString() ?: "",
            externalApiTimezoneOffsetMinutes = environment.config.propertyOrNull("sync-scheduler.external-api-timezone-offset-minutes")
                ?.getString()?.toInt() ?: 0,
        ),
        polynom  = PolynomSettings(environment.config.property("polynom.base-url").getString()),
        loodsman = LoodsmanSettings(environment.config.property("loodsman.base-url").getString()),
    )

    val settingsStore = AppSettingsStore(
        filePath = AppSettingsStore.resolvePath(
            environment.config.propertyOrNull("connection-settings.file")?.getString()
                ?: System.getenv("CONNECTION_SETTINGS_FILE")
        ),
        seed = seedSettings,
    )
    try {
        settingsStore.loadFromDisk()
    } catch (e: Exception) {
        log.warn("Failed to read connection settings file {}: {} — falling back to application.conf", settingsStore.filePath, e.message)
    }

    // 2. Managers own the connections; both start disconnected and are
    //    connected by background reconnect loops (never block startup).
    val databaseManager = DatabaseManager()
    val rabbitManager = RabbitManager()

    // 3. Application wiring.
    val config = AppConfig.create(
        sessionStore = InMemorySessionStore(),
        appSettingsStore = settingsStore,
        databaseManager = databaseManager,
        rabbitManager = rabbitManager,
        environment = environment,
    )

//    config.syncScheduler.start()

    launch {
        while (isActive) {
            delay(60_000.milliseconds)
            config.sessionStore.cleanup(
                expirationThresholdMs = 7 * 24 * 60 * 60 * 1000L
            )
            config.loodsmanSessionStore.cleanup(
                expirationThresholdMs = 7 * 24 * 60 * 60 * 1000L
            )
        }
    }

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
            cookie.httpOnly = true
            cookie.secure = false
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 7
        }
    }

    install(StatusPages) {
        exception<ServiceUnavailableException> { call, cause ->
            call.respondText(
                text = "503: ${cause.message}",
                status = HttpStatusCode.ServiceUnavailable,
            )
        }

        exception<RootNodeException> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    println("is dev: ${environment.config.property("ktor.deployment.is-dev").getString()}")

    install(SSE)
    configureHTTP()
    configureSerialization()
    configureRouting(config)
    devSessionRoute(config)
}
