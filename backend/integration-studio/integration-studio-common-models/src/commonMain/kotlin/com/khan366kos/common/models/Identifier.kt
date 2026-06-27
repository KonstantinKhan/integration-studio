package com.khan366kos.common.models

import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId

data class Identifier(
    val typeId: TypeId,
    val objectId: ObjectId
)
