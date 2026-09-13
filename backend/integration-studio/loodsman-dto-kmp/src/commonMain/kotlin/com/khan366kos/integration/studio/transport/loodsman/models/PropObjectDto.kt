package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class PropObjectDto(
    val idVersion: Int = 0,
    val type: String? = null,
    val document: Int = 0,
    val product: String? = null,
    val version: String? = null,
    val state: String? = null,
    val hasLink: Int = 0,
    val mainId: Int = 0,
)
