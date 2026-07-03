package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.Serializable

@Serializable
data class IAbleMeasureUnits(
    val units: List<IMeasureUnit>? = null
)
