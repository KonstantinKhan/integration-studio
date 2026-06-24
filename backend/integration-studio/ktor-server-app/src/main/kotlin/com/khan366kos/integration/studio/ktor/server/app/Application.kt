package com.khan366kos.integration.studio.ktor.server.app

import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.routes.devSessionRoute
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import com.khan366kos.integration.studio.ktor.server.app.session.InMemorySessionStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val sessionStore = InMemorySessionStore()
    val httpClient = HttpClient(CIO) {
        engine {
            maxConnectionsCount = 20
            endpoint {
                connectTimeout = 30_000
                socketTimeout = 30_000
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
        defaultRequest {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTP
                host = "172.23.14.181"
                port = 5100
                path("/api/v1/")
            }
        }
    }

    val config = AppConfig.create(
        sessionStore = sessionStore,
        httpClient = httpClient,
        baseUrl = "http://172.23.14.181:5100/api/v1"
    )

    launch {
        while (isActive) {
            delay(60_000.milliseconds)
            config.sessionStore.cleanup(
                expirationThresholdMs = 7 * 24 * 60 * 60 * 1000L
            )
        }
    }

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.domain = "localhost"
            cookie.extensions["SameSite"] = "lax"
            cookie.httpOnly = true
            cookie.secure = false
            cookie.maxAgeInSeconds = 60 * 60 * 24 * 7
        }
    }
    configureHTTP()
    configureSerialization()
    configureRouting(config)
    devSessionRoute(config)
}
