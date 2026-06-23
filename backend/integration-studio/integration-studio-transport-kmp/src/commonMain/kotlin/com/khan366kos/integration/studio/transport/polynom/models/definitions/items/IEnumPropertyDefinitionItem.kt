package com.khan366kos.integration.studio.transport.polynom.models.definitions.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IEnumPropertyDefinitionItem(
    @SerialName("id")
    val id: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("position")
    val position: Int,

    @SerialName("value")
    val value: String? = null,
)
