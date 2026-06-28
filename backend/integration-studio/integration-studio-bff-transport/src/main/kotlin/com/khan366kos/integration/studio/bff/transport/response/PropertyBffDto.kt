package com.khan366kos.integration.studio.bff.transport.response

import com.khan366kos.integration.studio.bff.transport.PropertyValueBffDto
import kotlinx.serialization.Serializable

@Serializable
data class PropertyBffDto(
    val name: String,
    val value: PropertyValueBffDto,
    val typeId: Int,
    val objectId: Int
)
