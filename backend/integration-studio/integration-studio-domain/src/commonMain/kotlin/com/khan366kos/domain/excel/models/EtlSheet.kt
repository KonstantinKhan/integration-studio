package com.khan366kos.domain.excel.models

import com.khan366kos.domain.excel.models.simple.EtlTableHeader
import com.khan366kos.domain.excel.models.simple.EtlSheetTitle

data class EtlSheet(
    val title: EtlSheetTitle,
    val headers: List<EtlTableHeader>,
    val entriesSize: Int
)