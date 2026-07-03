package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import com.khan366kos.integration.studio.transport.polynom.models.ValueFrom
import com.khan366kos.integration.studio.transport.polynom.models.ValueSingle
import com.khan366kos.integration.studio.transport.polynom.models.ValueTo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IDateTimeValue(
    val value: String,
    val useTime: Boolean,
    val objectId: Int? = null,
    val typeId: Int? = null,
    val dataType: Int? = null,
    val valueFrom: ValueFrom? = null,
    val valueTo: ValueTo? = null,
    val valueSingle: ValueSingle? = null,
)
