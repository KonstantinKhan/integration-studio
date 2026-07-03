package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import kotlinx.serialization.Serializable

@Serializable
data class IOpticValue(
    val ambient: Double,
    val diffuse: Double,
    val emission: Double,
    val shininess: Double,
    val specularity: Double,
    val transparency: Double
)
