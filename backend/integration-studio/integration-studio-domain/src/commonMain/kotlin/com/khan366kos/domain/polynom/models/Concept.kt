package com.khan366kos.domain.polynom.models

import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import kotlinx.serialization.Serializable

@Serializable
data class Concept(
    val name: String,
    val objectId: ObjectId,
    val typeId: TypeId
)
