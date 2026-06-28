package com.khan366kos.domain.requests

import com.khan366kos.domain.models.simple.ObjectId
import kotlinx.serialization.Serializable

@Serializable
data class ElementDeleteRequest(
    val elementIds: List<ObjectId>,
)
