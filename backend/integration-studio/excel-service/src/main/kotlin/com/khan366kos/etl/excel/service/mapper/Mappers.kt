package com.khan366kos.etl.excel.service.mapper

import com.khan366kos.common.excel.models.EtlSheet
import com.khan366kos.common.excel.models.EtlWorkbook
import com.khan366kos.common.excel.models.simple.EtlTableHeader
import com.khan366kos.common.excel.models.simple.EtlSheetTitle
import com.khan366kos.etl.excel.service.entriesSize
import com.khan366kos.etl.excel.service.headers
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

suspend fun Sheet.toEtl(): EtlSheet = EtlSheet(
    title = EtlSheetTitle(sheetName),
    headers = headers().map { header ->
        EtlTableHeader(header)
    },
    entriesSize = entriesSize()
)

suspend fun XSSFWorkbook.toEtl(): EtlWorkbook = EtlWorkbook(
    sheets = (0 until numberOfSheets).map { index ->
        getSheetAt(index).toEtl()
    }
)