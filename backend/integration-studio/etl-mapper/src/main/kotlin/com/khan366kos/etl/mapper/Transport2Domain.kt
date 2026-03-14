package com.khan366kos.etl.mapper

import com.khan366kos.common.excel.models.EtlSheet
import com.khan366kos.common.excel.models.EtlWorkbook
import com.khan366kos.common.excel.models.simple.EtlSheetTitle
import com.khan366kos.common.excel.models.simple.EtlTableHeader
import com.khan366kos.common.models.auth.AuthorizationCredentials
import com.khan366kos.common.models.business.*
import com.khan366kos.common.models.business.elementGroup.ElementGroup
import com.khan366kos.common.models.business.elementGroup.simple.ElementGroupId
import com.khan366kos.common.models.business.elementGroup.simple.ElementGroupName
import com.khan366kos.common.models.business.elementGroup.simple.ElementGroupPath
import com.khan366kos.common.models.definitions.StorageDefinition
import com.khan366kos.common.models.simple.*
import com.khan366kos.integration.studio.transport.models.AuthorizationRequestTransport
import com.khan366kos.integration.studio.transport.models.ElementCatalogTransport
import com.khan366kos.integration.studio.transport.models.ElementGroupTransport
import com.khan366kos.integration.studio.transport.models.ElementTransport
import com.khan366kos.integration.studio.transport.models.EtlSheetTransport
import com.khan366kos.integration.studio.transport.models.EtlWorkbookTransport
import com.khan366kos.integration.studio.transport.models.NamedObjectTransport
import com.khan366kos.integration.studio.transport.models.ReferenceTransport
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.ViewpointCatalogTransport

fun EtlWorkbookTransport.toEtlWorkbook(): EtlWorkbook =
    EtlWorkbook(
        sheets = this.sheets.map { it.toEtlSheet() }
    )

fun EtlSheetTransport.toEtlSheet(): EtlSheet =
    EtlSheet(
        title = EtlSheetTitle(this.title),
        headers = this.headers.map { EtlTableHeader(it) },
        entriesSize = this.entriesSize
    )

fun StorageDefinitionTransport.toStorageDefinition(): StorageDefinition =
    StorageDefinition(
        storageId = this.storageId,
        displayName = this.displayName
    )

fun AuthorizationRequestTransport.toAuthorizationCredentials(): AuthorizationCredentials =
    AuthorizationCredentials(
        username = this.username,
        password = this.password,
        storageId = this.storageId
    )

fun NamedObjectTransport.toPathElement(): PathElement =
    PathElement(
        objectId = ObjectId(objectId),
        typeId = TypeId(typeId),
        name = ElementName(name ?: "")
    )

fun ElementCatalogTransport.toCatalog(): Catalog =
    Catalog(
        id = ReferenceId(id ?: ""),
        classId = ReferenceId(classId ?: ""),
        name = ElementName(name ?: ""),
        objectId = ObjectId(objectId),
        typeId = TypeId(typeId),
        iconCode = IconCode(iconCode),
        iconColor = IconColor(iconColor ?: 0),
        writeAccess = WriteAccess(writeAccess),
        path = path?.map { it.toPathElement() } ?: emptyList(),
        count = count,
        reference = Identifier(
            objectId = ObjectId(reference.objectId),
            typeId = TypeId(reference.typeId)
        ),
        isEntry = isEntry ?: false
    )

fun ViewpointCatalogTransport.toViewpointCatalog(): ViewpointCatalog =
    ViewpointCatalog(
        id = ReferenceId(id ?: ""),
        classId = ReferenceId(classId ?: ""),
        name = ElementName(name ?: ""),
        objectId = ObjectId(objectId),
        typeId = TypeId(typeId),
        iconCode = IconCode(iconCode),
        iconColor = IconColor(iconColor ?: 0),
        writeAccess = WriteAccess(writeAccess),
        path = path?.map { it.toPathElement() } ?: emptyList(),
        count = count,
        reference = reference?.let {
            Identifier(
                objectId = ObjectId(it.objectId),
                typeId = TypeId(it.typeId)
            )
        }
    )

fun ReferenceTransport.toReference(): Reference =
    Reference(
        id = ReferenceId(id ?: ""),
        name = ElementName(name ?: ""),
        description = Description(description ?: ""),
        objectId = ObjectId(objectId),
        typeId = TypeId(typeId),
        iconCode = IconCode(iconCode),
        iconColor = IconColor(iconColor ?: 0),
        writeAccess = WriteAccess(writeAccess),
        path = path?.map { it.toPathElement() } ?: emptyList(),
        documentCatalog = documentCatalog?.toCatalog(),
        viewpointCatalog = viewpointCatalog?.toViewpointCatalog()
    )

fun ElementGroupTransport.toElementGroup(): ElementGroup =
    ElementGroup(
        name = ElementGroupName(name ?: ""),
        iconCode = IconCode(iconCode),
        iconColor = IconColor(iconColor ?: 0),
        writeAccess = WriteAccess(writeAccess),
        description = Description(description ?: ""),
        applicability = Applicability(applicability),
        id = ElementGroupId(id ?: ""),
        objectId = ObjectId(objectId),
        typeId = TypeId(typeId),
        path = path?.map { it.toPathElementGroup() } ?: emptyList(),
        parentCatalog = Identifier(ObjectId(parentCatalog?.objectId ?: 0), TypeId(parentCatalog?.typeId ?: 0)),
        parentGroup = Identifier(ObjectId(parentCatalog?.objectId ?: 0), TypeId(parentCatalog?.typeId ?: 0)),
        hasObjects = hasObjects,
        count = count,
        isEntry = isEntry ?: false,
        classId = classId ?: "",
        isAllPartSizesTab = isAllPartSizesTab ?: false,
    )

fun NamedObjectTransport.toPathElementGroup(): ElementGroupPath =
    ElementGroupPath(
        objectId = ObjectId(objectId),
        typeId = TypeId(typeId),
        name = NamePath(name ?: "")
    )

fun ElementTransport.toElement(): Element = Element(
    objectId = ObjectId(objectId),
    typeId = TypeId(typeId),
    name = ElementName(name),
    iconCode = IconCode(iconCode),
    iconColor = IconColor(iconColor ?: 0),
    path = path?.map { it.toPathElement() } ?: emptyList(),
    id = PathId(id ?: ""),
    ownerGroup = ownerGroup?.let { OwnerGroup(ObjectId(it.objectId), TypeId(it.typeId)) } ?: OwnerGroup.NONE,
    applicability = Applicability(applicability),
    isMaterial = isMaterial,
    isAssortmentInstancesOwner = isAssortmentInstancesOwner
)