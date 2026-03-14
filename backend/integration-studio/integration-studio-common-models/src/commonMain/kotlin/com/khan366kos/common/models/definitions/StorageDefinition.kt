package com.khan366kos.common.models.definitions

import kotlinx.serialization.Serializable

@Serializable
data class StorageDefinition(
    val storageId: String,
    val displayName: String?
)