package com.khan366kos.integration.studio.transport.polynom.request.catalog

import kotlinx.serialization.Serializable

@Serializable
data class IGetByIdRequest(
    val objectId: Int,
    val typeId: Int
)
