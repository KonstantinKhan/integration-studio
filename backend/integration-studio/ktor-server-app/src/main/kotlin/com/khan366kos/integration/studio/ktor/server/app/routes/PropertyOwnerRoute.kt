package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.propertyOwner(config: AppConfig): Route = route("property-owner") {
    post("/get-properties") {
        val service = config.polynomApplicationService
        val request = call.receive<OwnerRequest>()
        val results = service.polynomElement(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, results)
    }
}
