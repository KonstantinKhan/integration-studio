package com.khan366kos.domain.polynom.models

import com.khan366kos.domain.models.simple.Description
import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.ReferenceId
import com.khan366kos.domain.models.simple.TypeId

data class Reference(
    val id: ReferenceId,
    val name: ElementName,
    val description: Description,
    val objectId: ObjectId,
    val typeId: TypeId,
)