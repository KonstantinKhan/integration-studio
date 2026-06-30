package com.khan366kos.domain.polynom

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PolynomElement(
    val designation: String,
    val classifierCode: Long,
    val changeDate: LocalDateTime,
    val properties: List<PropertyResult>,
    val typeId: Int,
    val objectId: Int,
)
