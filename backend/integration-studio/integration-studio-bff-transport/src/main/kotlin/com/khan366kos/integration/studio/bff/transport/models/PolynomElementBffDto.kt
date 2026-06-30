package com.khan366kos.integration.studio.bff.transport.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PolynomElementBffDto(
    val designation: String,
    val classifierCode: Long,
    val changeDate: LocalDateTime,
    val properties: List<PropertyBffDto>,
    val typeId: Int,
    val objectId: Int,
)