package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StorageDefinitionTransport(
    @SerialName("storageId")
    val storageId: String,

    @SerialName("displayName")
    val displayName: String?
)
