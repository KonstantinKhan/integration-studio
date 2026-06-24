package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import com.khan366kos.integration.studio.transport.polynom.models.ValueFrom
import com.khan366kos.integration.studio.transport.polynom.models.ValueSingle
import com.khan366kos.integration.studio.transport.polynom.models.ValueTo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IDateTimeValue(
    @SerialName("value")
    val value: String,

    @SerialName("useTime")
    val useTime: Boolean,

    @SerialName("objectId")
    val objectId: Int? = null,

    @SerialName("typeId")
    val typeId: Int? = null,

    @SerialName("valueFrom")
    val valueFrom: ValueFrom? = null,

    @SerialName("valueTo")
    val valueTo: ValueTo? = null,

    @SerialName("valueSingle")
    val valueSingle: ValueSingle? = null,
)
