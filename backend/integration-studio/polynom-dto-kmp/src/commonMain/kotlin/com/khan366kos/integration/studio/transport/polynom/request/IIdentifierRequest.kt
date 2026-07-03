package com.khan366kos.integration.studio.transport.polynom.request

import kotlinx.serialization.Serializable

@Serializable
data class IIdentifierRequest(
    val objectId: Int,
    val typeId: Int
)
