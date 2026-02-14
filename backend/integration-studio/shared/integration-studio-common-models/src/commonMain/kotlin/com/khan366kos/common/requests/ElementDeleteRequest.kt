package com.khan366kos.common.requests

import com.khan366kos.common.models.simple.ObjectId
import kotlinx.serialization.Serializable

@Serializable
data class ElementDeleteRequest(
    val elementIds: List<ObjectId>,
)
