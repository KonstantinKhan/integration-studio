package com.khan366kos.common.models.business

import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Reference(
    val id: ReferenceId,
    val name: ElementName,
    val description: Description,
    val objectId: ObjectId,
    val typeId: TypeId,
    val iconCode: IconCode,
    val iconColor: IconColor,
    val writeAccess: WriteAccess,
    val path: List<PathElement>,
    val documentCatalog: Catalog?,
    val viewpointCatalog: ViewpointCatalog?
)