package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import kotlinx.serialization.Serializable

@Serializable
data class Identifier(
    val objectId: ObjectId,
    val typeId: TypeId
)
