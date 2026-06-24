package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.transport.polynom.request.IPropertySearchRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.search(service: PolynomApplicationService): Route = route("search") {
    post("execute-property-search") {
        val request = call.receive<IPropertySearchRequest>()
        println("request: $request")
        val result = service.executePropertySearch(call.userSession.id, request)
        println("result: $result")
        call.respond(HttpStatusCode.OK, result)
    }

    post("changed-objects") {
        val request = call.receive<IPropertySearchRequest>()
        val result = service.searchChangedObjects(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, result)
    }
}
