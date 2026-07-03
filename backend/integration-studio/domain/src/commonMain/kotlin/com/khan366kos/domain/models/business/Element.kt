package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.*

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