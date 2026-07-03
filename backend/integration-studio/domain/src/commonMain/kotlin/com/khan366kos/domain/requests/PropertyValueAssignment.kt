package com.khan366kos.domain.requests

import com.khan366kos.domain.models.business.Identifier

data class PropertyValueAssignment(
    val value: Identifier,
    val contract: Identifier,
    val definition: Identifier
)