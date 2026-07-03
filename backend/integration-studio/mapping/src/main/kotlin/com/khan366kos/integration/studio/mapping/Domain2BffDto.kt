package com.khan366kos.integration.studio.mapping

import com.khan366kos.domain.models.business.Catalog
import com.khan366kos.domain.polynom.Node
import com.khan366kos.domain.polynom.PolynomElement
import com.khan366kos.domain.polynom.PropertyResult
import com.khan366kos.domain.polynom.PropertyValueSimple
import com.khan366kos.domain.polynom.models.Reference
import com.khan366kos.integration.studio.bff.dto.models.CatalogBffDto
import com.khan366kos.integration.studio.bff.dto.models.PropertyValueBffDto
import com.khan366kos.integration.studio.bff.dto.response.NodeResponseBffDto
import com.khan366kos.integration.studio.bff.dto.models.PolynomElementBffDto
import com.khan366kos.integration.studio.bff.dto.models.PropertyBffDto
import com.khan366kos.integration.studio.bff.dto.response.CreateReferenceResponse

fun Node.toBffDto() = NodeResponseBffDto(
    name = name,
    typeId = typeId.asInt(),
    objectId = objectId.asInt()
)

fun PolynomElement.toBffDto() = PolynomElementBffDto(
    designation = designation,
    classifierCode = classifierCode,
    changeDate = changeDate,
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

fun Reference.toBffDto() = CreateReferenceResponse(
    name = name.asString(),
    typeId = typeId.asInt(),
    objectId = objectId.asInt()
)

fun Catalog.toBffDto() = CatalogBffDto(
    name = name.asString(),
    typeId = typeId.asInt(),
    objectId = objectId.asInt()
)