package com.khan366kos.integration.studio.mapping

import com.khan366kos.domain.polynom.Node
import com.khan366kos.domain.polynom.PolynomElement
import com.khan366kos.domain.polynom.PropertyResult
import com.khan366kos.domain.polynom.PropertyValueSimple
import com.khan366kos.integration.studio.bff.transport.models.PropertyValueBffDto
import com.khan366kos.integration.studio.bff.transport.response.NodeResponseBffDto
import com.khan366kos.integration.studio.bff.transport.models.PolynomElementBffDto
import com.khan366kos.integration.studio.bff.transport.models.PropertyBffDto

fun Node.toBffDto() = NodeResponseBffDto(
    name = name,
    typeId = typeId.asInt(),
    objectId = objectId.asInt()
)

fun PolynomElement.toBffDto() = PolynomElementBffDto(
    name = name,
    properties = properties.map { it.toBffDto() },
    typeId = typeId,
    objectId = objectId
)

fun PropertyResult.toBffDto() = PropertyBffDto(
    name = name,
    value = value.toBffDto(),
    typeId = typeId,
    objectId = objectId
)

fun PropertyValueSimple.toBffDto() = when (this) {
    is PropertyValueSimple.StringValSimple -> PropertyValueBffDto.StringValueBffDto(data = data)
    is PropertyValueSimple.BooleanValSimple -> PropertyValueBffDto.BooleanValueBffDto(data = data)
    is PropertyValueSimple.EmptyValSimple -> PropertyValueBffDto.EmptyValueBffDto(data = data)
    is PropertyValueSimple.DateTimeValSimple -> PropertyValueBffDto.DateTimeValueBffDto(data = data)
    is PropertyValueSimple.EnumValSimple -> PropertyValueBffDto.EnumValueBffDto(data = data)
    else -> throw IllegalStateException("Unknown PropertyValue BffDto")
}