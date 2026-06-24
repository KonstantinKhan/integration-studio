package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.propertyOwner(service: PolynomApplicationService): Route = route("property-owner") {
    post("/get-properties") {
        val request = call.receive<OwnerRequest>()
        val results = service.getPropertiesEnriched(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, results)
    }
}
