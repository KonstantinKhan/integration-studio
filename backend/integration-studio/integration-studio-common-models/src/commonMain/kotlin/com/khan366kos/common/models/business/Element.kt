package com.khan366kos.common.models.business

import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Element(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: ElementName,
    val iconCode: IconCode,
    val iconColor: IconColor,
    val path: List<PathElement>,
    val id: PathId,
    val ownerGroup: OwnerGroup,
    val applicability: Applicability,
    val isMaterial: Boolean,
    val isAssortmentInstancesOwner: Boolean
)