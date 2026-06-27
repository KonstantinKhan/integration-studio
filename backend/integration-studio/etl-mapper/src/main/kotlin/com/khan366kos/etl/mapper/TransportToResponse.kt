package com.khan366kos.etl.mapper

import com.khan366kos.integration.studio.transport.models.IReference
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceResponse

fun IReference.toCreateReferenceResponse() = CreateReferenceResponse(
    id = id,
    name = name,
    typeId = typeId,
    objectId = objectId,
)