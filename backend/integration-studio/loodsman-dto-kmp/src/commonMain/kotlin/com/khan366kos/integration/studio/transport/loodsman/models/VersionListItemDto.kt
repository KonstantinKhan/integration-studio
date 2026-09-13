package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class VersionListItemDto(
    val idVersion: Int = 0,
    val type: String? = null,
    val product: String? = null,
    val version: String? = null,
    val dateOfCreate: String? = null,
    val state: String? = null,
    val owner: String? = null,
)
