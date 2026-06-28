package com.khan366kos.domain.models.definitions

import kotlinx.serialization.Serializable

@Serializable
data class StorageDefinition(
    val storageId: String,
    val displayName: String?
)