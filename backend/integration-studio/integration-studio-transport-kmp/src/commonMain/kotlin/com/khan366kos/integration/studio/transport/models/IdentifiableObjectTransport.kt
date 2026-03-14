package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentifiableObjectTransport(
    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int
)