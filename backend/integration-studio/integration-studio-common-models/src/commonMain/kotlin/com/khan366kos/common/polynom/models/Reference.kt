package com.khan366kos.common.polynom.models

import com.khan366kos.common.models.business.Catalog
import com.khan366kos.common.models.business.PathElement
import com.khan366kos.common.models.business.ViewpointCatalog
import com.khan366kos.common.models.simple.Description
import com.khan366kos.common.models.simple.ElementName
import com.khan366kos.common.models.simple.IconCode
import com.khan366kos.common.models.simple.IconColor
import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.ReferenceId
import com.khan366kos.common.models.simple.TypeId
import com.khan366kos.common.models.simple.WriteAccess
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
    val documentCatalog: Catalog? = null,
    val viewpointCatalog: ViewpointCatalog? = null
)