package com.khan366kos.common.models.business

import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Catalog(
    val id: ReferenceId,
    val classId: ReferenceId,
    val name: ElementName,
    val objectId: ObjectId,
    val typeId: TypeId,
    val iconCode: IconCode,
    val iconColor: IconColor,
    val writeAccess: WriteAccess,
    val path: List<PathElement>,
    val count: Int,
    val reference: Identifier,
    val isEntry: Boolean
)