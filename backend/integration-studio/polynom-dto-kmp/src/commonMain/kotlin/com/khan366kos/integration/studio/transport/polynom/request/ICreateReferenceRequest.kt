package com.khan366kos.integration.studio.transport.polynom.request

import kotlinx.serialization.Serializable

@Serializable
data class ICreateReferenceRequest(
    val name: String,
)