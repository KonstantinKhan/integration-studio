package com.khan366kos.domain.polynom

import kotlinx.serialization.Serializable

@Serializable
data class PolynomElement(
    val name: String,
    val properties: List<PropertyResult>,
    val typeId: Int,
    val objectId: Int,
)
