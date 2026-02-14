package com.khan366kos.etl.ktor.server.app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("MyCustomHeader")
        allowCredentials = true

        // Явно указываем допустимые хосты вместо anyHost() для совместимости с allowCredentials
        allowHost("localhost:3000", schemes = listOf("http"))
        allowHost("localhost:8081", schemes = listOf("http"))
    }
}
