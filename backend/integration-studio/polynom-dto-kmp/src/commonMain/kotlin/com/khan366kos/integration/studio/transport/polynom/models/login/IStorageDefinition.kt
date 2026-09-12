package com.khan366kos.integration.studio.transport.polynom.models.login

import kotlinx.serialization.Serializable

@Serializable
data class IStorageDefinition(
    val storageId: String,
    val displayName: String? = null,
)