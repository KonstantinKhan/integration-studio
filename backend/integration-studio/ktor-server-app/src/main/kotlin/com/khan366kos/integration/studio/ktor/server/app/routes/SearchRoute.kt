package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.common.models.PropertyResult
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
        val request = call.receive<IPropertySearchRequest>()
        val response = mutableListOf<List<PropertyResult>>()
        config.backgroundScope.launch {
            service.searchObjects(call.userSession.id, request)
                .collect { obj ->
                    response.add(obj)
                }
        }.join()

        call.respond(HttpStatusCode.Accepted, response)
    }
}
