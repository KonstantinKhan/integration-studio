package com.khan366kos.etl.ktor.server.app

import com.khan366kos.etl.ktor.server.app.com.khan366kos.etl.ktor.server.app.config.AppConfig
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import com.khan366kos.etl.ktor.server.app.session.InMemorySessionStore
import com.khan366kos.etl.polynom.bff.PolynomClient
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val sessionStore = InMemorySessionStore()
    val polynomClient = PolynomClient()
    val config = AppConfig(
        sessionStore = sessionStore,
        polynomClient = polynomClient
    )

    launch(Dispatchers.Default) {
        polynomClient.credentialsUpdates.collect { (sessionId, credentials) ->
            sessionStore.updateCredentials(sessionId, credentials)
        }
    }

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