package com.khan366kos.etl.mapper

import com.khan366kos.common.excel.models.EtlSheet
import com.khan366kos.common.excel.models.EtlWorkbook
import com.khan366kos.common.excel.models.simple.EtlSheetTitle
import com.khan366kos.common.excel.models.simple.EtlTableHeader
import com.khan366kos.common.models.auth.AuthorizationCredentials
import com.khan366kos.common.models.business.*
import com.khan366kos.common.models.definitions.StorageDefinition
import com.khan366kos.common.models.simple.*
import com.khan366kos.integration.studio.transport.models.AuthorizationRequestTransport
import com.khan366kos.integration.studio.transport.models.DocumentCatalogTransport
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

fun DocumentCatalogTransport.toDocumentCatalog(): DocumentCatalog =
    DocumentCatalog(
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
        documentCatalog = documentCatalog?.toDocumentCatalog(),
        viewpointCatalog = viewpointCatalog?.toViewpointCatalog()
    )