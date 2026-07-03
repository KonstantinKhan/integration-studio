package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import kotlinx.serialization.Serializable

@Serializable
data class IColorValue(
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int
)
