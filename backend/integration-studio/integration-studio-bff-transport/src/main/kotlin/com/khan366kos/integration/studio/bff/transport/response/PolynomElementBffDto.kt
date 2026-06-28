package com.khan366kos.integration.studio.bff.transport.response

import kotlinx.serialization.Serializable

@Serializable
data class PolynomElementBffDto(
    val name: String,
    val properties: List<PropertyBffDto>,
    val typeId: Int,
    val objectId: Int,
)
