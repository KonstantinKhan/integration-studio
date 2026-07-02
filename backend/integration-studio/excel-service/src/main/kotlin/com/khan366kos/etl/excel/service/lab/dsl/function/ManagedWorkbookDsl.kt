package com.khan366kos.etl.excel.service.lab.dsl.function

import com.khan366kos.etl.excel.service.lab.ManagedWorkbookResult
import com.khan366kos.etl.excel.service.lab.dsl.builders.ManagedWorkbookBuilder

@DslMarker
annotation class ManagedWorkbookDsl
suspend fun useManagedWorkbook(block: ManagedWorkbookBuilder.() -> Unit): ManagedWorkbookResult =
    ManagedWorkbookBuilder().apply(block).build()