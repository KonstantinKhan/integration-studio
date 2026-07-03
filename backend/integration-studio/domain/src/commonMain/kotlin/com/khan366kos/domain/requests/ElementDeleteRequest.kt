package com.khan366kos.domain.requests

import com.khan366kos.domain.models.simple.ObjectId

data class ElementDeleteRequest(
    val elementIds: List<ObjectId>,
)
