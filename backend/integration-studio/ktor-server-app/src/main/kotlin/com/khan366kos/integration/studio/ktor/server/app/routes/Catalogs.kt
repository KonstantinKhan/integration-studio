package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import com.khan366kos.integration.studio.mapping.toBffDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlin.text.toInt

fun Route.catalogs(service: PolynomApplicationService): Route = route("catalogs") {
    get {
        try {
            val referenceTypeId = call.parameters["referenceTypeId"]?.toInt()
            val referenceObjectId = call.parameters["referenceObjectId"]?.toInt()
            val typeId = call.parameters["typeId"]?.toInt()
            val objectId = call.parameters["objectId"]?.toInt()

            if (typeId == null && objectId == null && referenceTypeId != null && referenceObjectId != null) {
                val catalogs = service.catalogs(
                    call.userSession.id,
                    referenceTypeId,
                    referenceObjectId
                )
                call.respond(HttpStatusCode.OK, catalogs.map { it.toBffDto() })
            } else {
                val catalog = service.catalog(
                    call.userSession.id,
                    objectId!!,
                    typeId!!
                )
                call.respond(HttpStatusCode.OK, catalog.toBffDto())
            }

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Ошибка получения каталогов: ${e.message}")
            )
        }
    }
}