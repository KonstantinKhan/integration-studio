package com.khan366kos.integration.studio.bff.transport.response

import kotlinx.serialization.Serializable

@Serializable
data class NodeResponseBffDto(
    val name: String,
    val typeId: Int,
    val objectId: Int
)
