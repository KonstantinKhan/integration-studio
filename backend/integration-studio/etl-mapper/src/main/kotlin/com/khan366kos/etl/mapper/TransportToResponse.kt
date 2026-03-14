package com.khan366kos.etl.mapper

import com.khan366kos.integration.studio.transport.models.ReferenceTransport
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceResponse

fun ReferenceTransport.toCreateReferenceResponse() = CreateReferenceResponse(
    id = id,
    name = name,
    typeId = typeId,
    objectId = objectId,
)