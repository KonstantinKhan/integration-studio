package com.khan366kos.domain.requests

import com.khan366kos.domain.models.business.Identifier
import kotlinx.serialization.Serializable

@Serializable
data class CreateElementRequest(
    val parentGroup: Identifier,
    val name: String = ""
)
