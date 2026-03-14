package com.khan366kos.integration.studio.transport.polynom.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReferenceResponse(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("typeId")
    val typeId: Int,
    @SerialName("objectId")
    val objectId: Int,
)
