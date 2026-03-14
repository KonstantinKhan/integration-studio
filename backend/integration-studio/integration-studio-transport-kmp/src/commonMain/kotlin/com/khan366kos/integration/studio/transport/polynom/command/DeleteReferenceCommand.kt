package com.khan366kos.integration.studio.transport.polynom.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteReferenceCommand (
    @SerialName("objectId")
    val objectId: Int,
    @SerialName("typeId")
    val typeId: Int
)