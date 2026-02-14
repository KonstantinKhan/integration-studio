package com.khan366kos.common.requests

import com.khan366kos.common.models.business.Identifier
import kotlinx.serialization.Serializable

@Serializable
data class PropertyValueAssignment(
    val value: Identifier,
    val contract: Identifier,
    val definition: Identifier
)