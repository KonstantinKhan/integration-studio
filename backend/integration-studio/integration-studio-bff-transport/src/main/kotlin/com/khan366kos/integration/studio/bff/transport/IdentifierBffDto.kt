package com.khan366kos.integration.studio.bff.transport

import kotlinx.serialization.Serializable

@Serializable
data class IdentifierBffDto(
    val typeId: Int,
    val objectId: Int
)
