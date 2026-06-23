package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertyOwnerRef(
    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("id")
    val id: String? = null,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("properties")
    val properties: List<IPropertyRef>? = null,
)
