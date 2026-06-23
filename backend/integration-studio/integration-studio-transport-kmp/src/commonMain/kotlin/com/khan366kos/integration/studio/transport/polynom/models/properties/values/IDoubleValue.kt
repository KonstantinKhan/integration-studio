package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IDoubleValue(
    @SerialName("mode")
    val mode: Int,

    @SerialName("value")
    val value: Double? = null,

    @SerialName("minValue")
    val minValue: Double? = null,

    @SerialName("maxValue")
    val maxValue: Double? = null,

    @SerialName("lowerTolerance")
    val lowerTolerance: Double? = null,

    @SerialName("upperTolerance")
    val upperTolerance: Double? = null,

    @SerialName("measureUnit")
    val measureUnit: IIdentifiableObject
)