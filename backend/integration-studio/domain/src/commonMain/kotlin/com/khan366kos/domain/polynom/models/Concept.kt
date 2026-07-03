package com.khan366kos.domain.polynom.models

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId

data class Concept(
    val name: ElementName,
    val objectId: ObjectId,
    val typeId: TypeId
)
