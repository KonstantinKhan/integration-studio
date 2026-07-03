package com.khan366kos.integration.studio.transport.polynom.request.concept

import kotlinx.serialization.Serializable

@Serializable
data class IGetByCodeRequest(
    val code: String,
)
