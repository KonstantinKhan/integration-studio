package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.common.exceptions.NodeException
import com.khan366kos.common.exceptions.RootNodeException
import com.khan366kos.common.models.Identifier
import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId
import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.mapping.toBffDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.tree(service: PolynomApplicationService): Route = route("tree") {
    get("/root") {
        val response = service.getClassification(call.userSession.id)
        val result =
            if (response.size == 1) response.first()
                .toBffDto() else throw RootNodeException()
        call.respond(HttpStatusCode.OK, result)
    }
    get("/nodes") {
        val typeId = call.parameters["typeId"]?.toIntOrNull()
        val objectId = call.parameters["objectId"]?.toIntOrNull()
        if (typeId != null && objectId != null) {
            val response =
                service.nodes(call.userSession.id, Identifier(TypeId(typeId), ObjectId(objectId))).map { it.toBffDto() }
            call.respond(HttpStatusCode.OK, response)
        } else throw NodeException()

    }
}