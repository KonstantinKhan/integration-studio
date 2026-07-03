package com.khan366kos.domain.polynom.models

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId

data class Group(
    val name: ElementName,
    val typeId: TypeId,
    val objectId: ObjectId,
)
