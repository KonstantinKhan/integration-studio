package com.khan366kos.common.responses

import com.khan366kos.common.models.business.OwnerGroup
import com.khan366kos.common.models.business.PathElement
import com.khan366kos.common.models.simple.Applicability
import com.khan366kos.common.models.simple.ElementName
import com.khan366kos.common.models.simple.IconCode
import com.khan366kos.common.models.simple.IconColor
import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.PathId
import com.khan366kos.common.models.simple.TypeId
import kotlinx.serialization.Serializable

@Serializable
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