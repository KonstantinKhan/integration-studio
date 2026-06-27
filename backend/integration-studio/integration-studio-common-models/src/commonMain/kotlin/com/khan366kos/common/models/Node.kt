package com.khan366kos.common.models

import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId

data class Node(
    val name: String,
    val typeId: TypeId,
    val objectId: ObjectId
)
