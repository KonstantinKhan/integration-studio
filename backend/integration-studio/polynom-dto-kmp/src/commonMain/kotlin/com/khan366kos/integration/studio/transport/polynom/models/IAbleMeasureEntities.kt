package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IAbleMeasureEntities(
    @SerialName("entities")
    val entities: List<IMeasureEntity>? = null
)
