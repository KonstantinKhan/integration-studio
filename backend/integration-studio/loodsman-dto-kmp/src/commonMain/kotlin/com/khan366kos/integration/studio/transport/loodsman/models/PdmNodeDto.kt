package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class PdmNodeDto(
    val id: Int = 0,
    val idType: Int = 0,
    val hasLink: Boolean = false,
    val product: String? = null,
    val version: String? = null,
    val idState: Int = 0,
    val idLock: Int = 0,
    val accessLevel: Int = 0,
    val label: Int = 0,
    val labelName: String? = null,
    val idLink: Int? = null,
    val idLinkType: Int? = null,
    val minQuantity: Double? = null,
    val maxQuantity: Double? = null,
)
