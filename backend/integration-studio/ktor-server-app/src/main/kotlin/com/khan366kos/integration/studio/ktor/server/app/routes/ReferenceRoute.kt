package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.bff.dto.command.CreateReferenceCommand
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import com.khan366kos.integration.studio.mapping.toBffDto
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.text.toInt

fun Route.references(service: PolynomApplicationService) = route("references") {

    post("/create") {
        try {
            val request = call.receive<CreateReferenceCommand>()
            val reference = service.referenceCreate(call.userSession.id, request.name)
            reference.toBffDto()
            call.respond(HttpStatusCode.Created, reference)
        } catch (e: Error) {
            println("Error: ${e.message}")
        }
    }
    post("/delete") {
        try {
            val request = call.receive<DeleteReferenceCommand>()
            val response = service.referenceDelete(call.userSession.id, request)
            call.respond(response.status)
        } catch (e: Error) {
            println("Error: ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
        }
    }
    get {
        try {
            val typeId = call.parameters["typeId"]?.toInt()
            val objectId = call.parameters["objectId"]?.toInt()

            if (typeId == null && objectId == null) {
                val references = service.references(call.userSession.id)
                call.respond(HttpStatusCode.OK, references.map { it.toBffDto() })
            } else {
                val reference = service.reference(
                    call.userSession.id,
                    IIdentifiableObject(
                        objectId!!,
                        typeId!!
                    )
                )
                call.respond(HttpStatusCode.OK, reference.toBffDto())
            }
        } catch (e: Exception) {
            application.log.error("Error fetching references: ${e.message}", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Ошибка получения справочников: ${e.message}")
            )
        }
    }
}