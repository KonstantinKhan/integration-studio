package com.khan366kos.integration.studio.mapping

import com.khan366kos.domain.models.business.Catalog
import com.khan366kos.domain.models.simple.Description
import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.polynom.Node
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.ReferenceId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.polynom.models.Concept
import com.khan366kos.domain.polynom.models.Reference
import com.khan366kos.integration.studio.transport.models.AppointedConceptDto
import com.khan366kos.integration.studio.transport.models.IReference
import com.khan366kos.integration.studio.transport.polynom.models.IClassificationTreeNode
import com.khan366kos.integration.studio.transport.polynom.models.catalog.IElementCatalog

fun AppointedConceptDto.toDomain(): Concept = Concept(
    name = concept.name ?: "Unknown",
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)

fun IClassificationTreeNode.toDomain() = Node(
    name = name ?: "Unknown",
    typeId = TypeId(nodeObject.typeId),
    objectId = ObjectId(nodeObject.objectId),
)

fun IReference.toDomain() = Reference(
    id = id?.let { ReferenceId(it) } ?: ReferenceId.NONE,
    name = name?.let { ElementName(it) } ?: ElementName.NONE,
    description = description?.let { Description(it) } ?: Description.NONE,
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)

fun IElementCatalog.toDomain() = Catalog(
    name = name?.let { ElementName(it) } ?: ElementName.NONE,
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)