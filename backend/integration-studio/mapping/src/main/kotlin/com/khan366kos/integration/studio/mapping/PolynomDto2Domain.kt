package com.khan366kos.integration.studio.mapping

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.polynom.Node
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.domain.polynom.models.Concept
import com.khan366kos.integration.studio.transport.models.AppointedConceptDto
import com.khan366kos.integration.studio.transport.models.IReference
import com.khan366kos.integration.studio.transport.polynom.models.IClassificationTreeNode
import com.khan366kos.integration.studio.transport.polynom.models.catalog.IElementCatalog
import com.khan366kos.integration.studio.transport.polynom.models.concept.IConcept

fun AppointedConceptDto.toDomain(): Concept = Concept(
    name = concept.name?.let { ElementName(it) } ?: ElementName.NONE,
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)

fun IClassificationTreeNode.toDomain() = Node(
    name = name ?: "Unknown",
    typeId = TypeId(nodeObject.typeId),
    objectId = ObjectId(nodeObject.objectId),
)

fun IReference.toDomain() = ClassifierTreeNode.Reference(
    name = name?.let { ElementName(it) } ?: ElementName.NONE,
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)

fun IElementCatalog.toDomain() = ClassifierTreeNode.Catalog(
    name = name?.let { ElementName(it) } ?: ElementName.NONE,
    typeId = TypeId(typeId),
    objectId = ObjectId(objectId)
)

fun IConcept.toDomain() = Concept(
    name = name?.let { ElementName(it) } ?: ElementName.NONE,
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId)
)