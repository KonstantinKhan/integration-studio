package com.khan366kos.integration.studio.bff.dto.models

import kotlinx.serialization.Serializable

@Serializable
data class ConceptBffDto(
    val name: String,
    val objectId: Int,
    val typeId: Int
)
