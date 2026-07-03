package com.khan366kos.integration.studio.transport.polynom.command

import kotlinx.serialization.Serializable

@Serializable
data class CreateReferenceResponse(
    val id: String? = null,
    val name: String? = null,
    val typeId: Int,
    val objectId: Int,
)
