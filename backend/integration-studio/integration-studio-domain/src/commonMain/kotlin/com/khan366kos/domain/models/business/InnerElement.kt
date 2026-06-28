package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.ElementName

data class InnerElement(
    val name: ElementName,
    val identifier: Identifier,
)
