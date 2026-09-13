package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkedObjectDto(
    val linkId: Int = 0,
    val versionId: Int = 0,
    val mainId: Int = 0,
    val product: String? = null,
    val version: String? = null,
    val linkTypeId: Int = 0,
    val minQuantity: Double? = null,
    val maxQuantity: Double? = null,
)
