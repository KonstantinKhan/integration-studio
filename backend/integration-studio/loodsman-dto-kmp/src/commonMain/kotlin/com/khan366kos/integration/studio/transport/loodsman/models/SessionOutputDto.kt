package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class SessionOutputDto(
    val sessionId: String? = null,
    val dbName: String? = null,
    val userId: Int = 0,
    val checkoutId: Int? = null,
    val isEditable: Boolean = false,
)
