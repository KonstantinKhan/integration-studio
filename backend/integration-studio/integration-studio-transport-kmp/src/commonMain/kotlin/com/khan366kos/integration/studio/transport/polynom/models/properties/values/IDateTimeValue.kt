package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IDateTimeValue(
    @SerialName("value")
    val value: String,

    @SerialName("useTime")
    val useTime: Boolean,
)
