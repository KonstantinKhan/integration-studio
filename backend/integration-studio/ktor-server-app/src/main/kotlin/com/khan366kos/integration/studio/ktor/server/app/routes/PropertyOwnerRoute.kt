package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.common.models.PropertyResult
import com.khan366kos.common.models.PropertyValue
import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.time.LocalDateTime

fun Route.propertyOwner(service: PolynomApplicationService): Route = route("property-owner") {
    post("/get-properties") {
        val request = call.receive<OwnerRequest>()
        val response = service.getProperties(call.userSession.id, request)

        val valueIndex = mutableMapOf<Pair<Int, Int>, PropertyValue>()

        response.values.stringProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.StringVal(value.value ?: "Unknown", value.typeId, value.objectId)
        }

        response.values.dateTimeProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.DateTimeVal(
                    LocalDateTime.parse(value.value.value) ?: LocalDateTime.MIN,
                    value.typeId,
                    value.objectId
                )
        }

        println("valueIndex: $valueIndex")

        val results = response.propertyOwner.properties?.mapNotNull { property ->
            val key = property.value?.typeId to property.value?.objectId
            val propertyValue = valueIndex[key]

            if (propertyValue != null) {
                val value = when (propertyValue) {
                    is PropertyValue.StringVal -> propertyValue
                    is PropertyValue.DateTimeVal -> propertyValue
                }

                PropertyResult(
                    name = property.name ?: "Unknown",
                    value = value,
                    typeId = property.typeId,
                    objectId = property.objectId
                )
            } else {
                null
            }
        }

        results?.forEach { result ->
            when (result.value) {
                is PropertyValue.StringVal -> println("${result.name}: ${result.value} (строка)")
                is PropertyValue.DateTimeVal -> println("${result.name}: ${result.value} (дата)")
            }
        }

        call.respond(HttpStatusCode.OK, response)
    }
}