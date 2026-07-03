package com.khan366kos.domain.models.business.elementGroup

import com.khan366kos.domain.models.business.Identifier
import com.khan366kos.domain.models.business.elementGroup.simple.ElementGroupId
import com.khan366kos.domain.models.business.elementGroup.simple.ElementGroupName
import com.khan366kos.domain.models.business.elementGroup.simple.ElementGroupPath
import com.khan366kos.domain.models.simple.Applicability
import com.khan366kos.domain.models.simple.Description
import com.khan366kos.domain.models.simple.IconCode
import com.khan366kos.domain.models.simple.IconColor
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.models.simple.WriteAccess

data class ElementGroup(
    val name: ElementGroupName,
    val iconCode: IconCode,
    val iconColor: IconColor,
    val writeAccess: WriteAccess,
    val description: Description,
    val applicability: Applicability,
    val id: ElementGroupId,
    val objectId: ObjectId,
    val typeId: TypeId,
    val path: List<ElementGroupPath>,
    val parentCatalog: Identifier,
    val parentGroup: Identifier,
    val hasObjects: Boolean,
    val count: Int,
    val isEntry: Boolean,
    val classId: String,
    val isAllPartSizesTab: Boolean,
    )