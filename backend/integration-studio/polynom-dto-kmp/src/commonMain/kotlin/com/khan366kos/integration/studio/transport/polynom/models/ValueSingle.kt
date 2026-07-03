package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValueSingle(
    @SerialName("value")
    val value: String,

    @SerialName("useTime")
    val useTime: Boolean,
)
