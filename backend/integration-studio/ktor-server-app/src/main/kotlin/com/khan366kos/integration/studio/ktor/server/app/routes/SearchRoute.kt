package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.transport.polynom.request.IPropertySearchRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.Instant

fun Route.search(service: PolynomApplicationService, config: AppConfig): Route = route("search") {
    post("execute-property-search") {
        val request = call.receive<IPropertySearchRequest>()
        val result = service.executePropertySearch(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, result)
    }

    post("changed-objects") {
        val request = call.receive<IPropertySearchRequest>()
        println("request: $request")
        val result = service.searchChangedObjects(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, result)
    }

    post("changes") {
        val duration = measureTimeMillis {
            val request = call.receive<IPropertySearchRequest>()
            config.backgroundScope.launch {
                service.searchObjects(call.userSession.id, request)
                    .collect { obj ->
//                        println("obj: $obj")
                    }
            }.join()
        }

        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        println("Время: $minutes мин $seconds сек")

        call.respond(HttpStatusCode.Accepted, "Processing started")
    }
}
