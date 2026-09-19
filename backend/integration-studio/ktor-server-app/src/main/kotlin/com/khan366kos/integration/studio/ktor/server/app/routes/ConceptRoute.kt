package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.transport.polynom.request.GroupRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.collections.emptyList

fun Route.concept(config: AppConfig): Route = route("/concept") {
    post("/get-by-concept-appointer") {
        val service = config.polynomApplicationService
        val request = call.receive<GroupRequestDto>()
        val response = service.conceptGetByConceptAppointer(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, response.appointedConcepts?.map { it.toDomain() } ?: emptyList())
    }
    get {
        val service = config.polynomApplicationService
        call.respond(
            HttpStatusCode.OK,
            service.concepts(
                call.userSession.id,
                listOf("Данные Классификатора", "Коэффициент преобразования из базовой ЕИ в:")
            )
        )
    }
}
