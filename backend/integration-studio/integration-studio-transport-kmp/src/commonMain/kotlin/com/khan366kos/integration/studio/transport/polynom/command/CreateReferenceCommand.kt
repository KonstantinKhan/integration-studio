package com.khan366kos.integration.studio.transport.polynom.command

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReferenceCommand(
    @SerialName("name")
    val name: String,
)