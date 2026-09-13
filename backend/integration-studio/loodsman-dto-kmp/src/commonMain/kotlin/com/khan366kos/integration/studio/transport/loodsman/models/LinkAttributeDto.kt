package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class LinkAttributeDto(
    val id: Int = 0,
    val name: String? = null,
    val value: String? = null,
)
