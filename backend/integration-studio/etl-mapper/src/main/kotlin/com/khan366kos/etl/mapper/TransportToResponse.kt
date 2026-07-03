package com.khan366kos.etl.mapper

import com.khan366kos.integration.studio.bff.dto.response.CreateReferenceResponse
import com.khan366kos.integration.studio.transport.models.IReference

fun IReference.toCreateReferenceResponse(): CreateReferenceResponse = CreateReferenceResponse(
    name = name ?: "",
    typeId = typeId,
    objectId = objectId,
)