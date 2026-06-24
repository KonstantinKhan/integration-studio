package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IStringPropertyValueRequest(
    @SerialName("objectId")
    val objectId: IIdentifiableObject,

    @SerialName("typeId")
    val typeId: IIdentifiableObject,

    @SerialName("value")
    val value: String
)
