package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.business.elementGroup.ElementGroup

data class GroupContent(
    val groups: List<ElementGroup> = emptyList(),
    val elements: List<Element> = emptyList(),
)