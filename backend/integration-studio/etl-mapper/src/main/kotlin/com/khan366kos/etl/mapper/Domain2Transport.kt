package com.khan366kos.etl.mapper

import com.khan366kos.common.excel.models.EtlSheet
import com.khan366kos.common.excel.models.EtlWorkbook
import com.khan366kos.common.models.auth.AuthorizationCredentials
import com.khan366kos.common.models.definitions.StorageDefinition
import com.khan366kos.integration.studio.transport.models.AuthorizationRequestTransport
import com.khan366kos.integration.studio.transport.models.EtlSheetTransport
import com.khan366kos.integration.studio.transport.models.EtlWorkbookTransport
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport

fun EtlWorkbook.toEtlWorkbookTransport(): EtlWorkbookTransport {
    return EtlWorkbookTransport(
        sheets = this.sheets.map { it.toEtlSheetTransport() }
    )
}

fun EtlSheet.toEtlSheetTransport(): EtlSheetTransport {
    return EtlSheetTransport(
        title = this.title.asString(),
        headers = this.headers.map { it.asString() },
        entriesSize = this.entriesSize
    )
}

fun StorageDefinition.toStorageDefinitionTransport(): StorageDefinitionTransport {
    return StorageDefinitionTransport(
        storageId = this.storageId,
        displayName = this.displayName
    )
}

fun AuthorizationCredentials.toAuthorizationRequestTransport(): AuthorizationRequestTransport {
    return AuthorizationRequestTransport(
        username = this.username,
        password = this.password,
        storageId = this.storageId
    )
}