package com.khan366kos.domain.responses

import com.khan366kos.domain.models.business.OwnerGroup
import com.khan366kos.domain.models.business.PathElement
import com.khan366kos.domain.models.simple.Applicability
import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.IconCode
import com.khan366kos.domain.models.simple.IconColor
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.PathId
import com.khan366kos.domain.models.simple.TypeId

data class ElementResponse(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: ElementName,
    val writeAccess: Boolean,
    val iconCode: IconCode,
    val iconColor: IconColor,
    val path: List<PathElement>,
    val id: PathId,
    val ownerGroup: OwnerGroup? = null,
    val applicability: Applicability,
    val isMaterial: Boolean,
    val isAssortmentInstancesOwner: Boolean
)