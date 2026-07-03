package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IIdentifiableObject(
    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int
)