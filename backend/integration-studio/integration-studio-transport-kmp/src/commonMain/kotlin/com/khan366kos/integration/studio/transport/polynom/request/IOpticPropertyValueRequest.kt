package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.properties.values.IOpticValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IOpticPropertyValueRequest(
    @SerialName("objectId")
    val objectId: IIdentifiableObject,

    @SerialName("typeId")
    val typeId: IIdentifiableObject,

    @SerialName("value")
    val value: IOpticValue
)
