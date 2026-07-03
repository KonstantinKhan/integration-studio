package com.khan366kos.integration.studio.bff.dto.models

import kotlinx.serialization.Serializable

@Serializable
data class PropertyBffDto(
    val name: String,
    val value: PropertyValueBffDto,
    val typeId: Int,
    val objectId: Int
)