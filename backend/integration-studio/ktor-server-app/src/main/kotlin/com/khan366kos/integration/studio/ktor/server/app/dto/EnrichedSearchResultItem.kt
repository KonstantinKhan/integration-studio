package com.khan366kos.integration.studio.ktor.server.app.dto

import com.khan366kos.common.models.PropertyResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EnrichedSearchResultItem(
    @SerialName("name")
    val name: String? = null,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("iconCode")
    val iconCode: Int,

    @SerialName("properties")
    val properties: List<PropertyResult> = emptyList(),
)
