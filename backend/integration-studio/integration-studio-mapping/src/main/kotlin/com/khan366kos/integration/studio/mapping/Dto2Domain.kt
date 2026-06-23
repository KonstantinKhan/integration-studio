package com.khan366kos.integration.studio.mapping

import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId
import com.khan366kos.common.polynom.models.Concept
import com.khan366kos.integration.studio.transport.models.AppointedConceptDto

fun AppointedConceptDto.toDomain(): Concept = Concept(
    name = concept.name ?: "Unknown",
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)