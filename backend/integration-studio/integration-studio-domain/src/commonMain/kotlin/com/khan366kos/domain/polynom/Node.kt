package com.khan366kos.domain.polynom

import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId

data class Node(
    val name: String,
    val typeId: TypeId,
    val objectId: ObjectId
)
