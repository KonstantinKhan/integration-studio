package com.khan366kos.integration.studio.mapping

import com.khan366kos.common.models.Node
import com.khan366kos.integration.studio.bff.transport.response.NodeResponseBffDto

fun Node.toBffDto() = NodeResponseBffDto(
    name = name,
    typeId = typeId.asInt(),
    objectId = objectId.asInt()
)