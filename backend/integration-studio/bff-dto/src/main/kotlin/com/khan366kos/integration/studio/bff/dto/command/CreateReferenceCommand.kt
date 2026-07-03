package com.khan366kos.integration.studio.bff.dto.command

import kotlinx.serialization.Serializable

@Serializable
data class CreateReferenceCommand(
    val name: String,
)