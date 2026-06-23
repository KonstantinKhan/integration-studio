package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.transport.polynom.request.GroupRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.concept(service: PolynomApplicationService): Route = route("concept") {
    post("/get-by-concept-appointer") {
        val request = call.receive<GroupRequestDto>()
        val response = service.conceptGetByConceptAppointer(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, response.appointedConcepts?.map { it.toDomain() } ?: emptyList())
    }
}
