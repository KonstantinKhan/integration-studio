package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.bff.transport.request.PolynomElementFromPeriodRequestBffDto
import com.khan366kos.integration.studio.bff.transport.models.PolynomElementBffDto
import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import com.khan366kos.integration.studio.mapping.toBffDto
import com.khan366kos.integration.studio.transport.polynom.request.search.IPropertySearchRequest
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
        val result = service.searchChangedObjects(call.userSession.id, request)
        call.respond(HttpStatusCode.OK, result)
    }

    post("changes") {
        val request = call.receive<PolynomElementFromPeriodRequestBffDto>()
        val response = mutableListOf<PolynomElementBffDto>()
        config.backgroundScope.launch {
            service.searchObjects(call.userSession.id, request)
                .collect { obj ->
                    response.add(obj.toBffDto())
                }
        }.join()

        call.respond(HttpStatusCode.Accepted, response)
    }
}
