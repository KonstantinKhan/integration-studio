package com.khan366kos.common.models.business

import com.khan366kos.common.models.business.elementGroup.ElementGroup
import kotlinx.serialization.Serializable

@Serializable
data class GroupContent(
    val groups: List<ElementGroup> = emptyList(),
    val elements: List<Element> = emptyList(),
)