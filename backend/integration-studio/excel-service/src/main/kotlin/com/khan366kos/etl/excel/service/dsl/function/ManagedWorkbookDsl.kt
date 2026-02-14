package com.khan366kos.etl.excel.service.dsl.function

import com.khan366kos.etl.excel.service.ManagedWorkbookResult
import com.khan366kos.etl.excel.service.dsl.builders.ManagedWorkbookBuilder

@DslMarker
annotation class ManagedWorkbookDsl
suspend fun useManagedWorkbook(block: ManagedWorkbookBuilder.() -> Unit): ManagedWorkbookResult =
    ManagedWorkbookBuilder().apply(block).build()