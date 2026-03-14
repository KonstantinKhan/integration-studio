package com.khan366kos.integration.studio.ktor.server.app

import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import com.khan366kos.integration.studio.ktor.server.app.session.InMemorySessionStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val sessionStore = InMemorySessionStore()
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTPS
                host = "delusively-altruistic-pangolin.cloudpub.ru"
                port = 443
                path("/api/v1/")
            }
        }
    }

    val config = AppConfig.create(
        sessionStore = sessionStore,
        httpClient = httpClient,
        baseUrl = "https://delusively-altruistic-pangolin.cloudpub.ru:443/api/v1"
    )

    launch {
        while (isActive) {
            delay(60_000)
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
}
