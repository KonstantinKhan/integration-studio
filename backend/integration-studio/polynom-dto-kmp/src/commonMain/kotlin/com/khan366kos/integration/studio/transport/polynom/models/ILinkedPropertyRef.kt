package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ILinkedPropertyRef(
    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("linkDefinitionEnd")
    val linkDefinitionEnd: IIdentifiableObject,

    @SerialName("linkableItem")
    val linkableItem: IIdentifiableObject,
)
