package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId

data class PathElement(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: ElementName
)