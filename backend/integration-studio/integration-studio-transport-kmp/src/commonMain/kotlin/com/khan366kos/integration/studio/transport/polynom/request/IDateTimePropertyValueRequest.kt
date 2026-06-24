package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.properties.values.IDateTimeValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IDateTimePropertyValueRequest(
    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int? = null,

    @SerialName("value")
    val value: IDateTimeValue
)
