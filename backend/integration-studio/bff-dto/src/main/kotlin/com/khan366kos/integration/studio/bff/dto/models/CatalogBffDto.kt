package com.khan366kos.integration.studio.bff.dto.models

import kotlinx.serialization.Serializable

@Serializable
data class CatalogBffDto(
    val name: String,
    val typeId: Int,
    val objectId: Int,
)