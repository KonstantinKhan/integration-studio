package com.khan366kos.domain.polynom.models

import com.khan366kos.domain.models.business.Catalog
import com.khan366kos.domain.models.business.PathElement
import com.khan366kos.domain.models.business.ViewpointCatalog
import com.khan366kos.domain.models.simple.Description
import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.IconCode
import com.khan366kos.domain.models.simple.IconColor
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.ReferenceId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.models.simple.WriteAccess
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