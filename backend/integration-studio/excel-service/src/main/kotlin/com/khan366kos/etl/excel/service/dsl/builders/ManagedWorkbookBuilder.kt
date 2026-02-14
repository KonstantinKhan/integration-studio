package com.khan366kos.etl.excel.service.dsl.builders

import com.khan366kos.etl.excel.service.ManagedWorkbook
import com.khan366kos.etl.excel.service.ManagedWorkbookResult
import com.khan366kos.etl.excel.service.dsl.function.ManagedWorkbookDsl
import com.khan366kos.etl.excel.service.types.ManagedWorkbookAction
import kotlin.use

@ManagedWorkbookDsl
class ManagedWorkbookBuilder {
    var path: String? = null
    private val actions: MutableList<ManagedWorkbookAction> = mutableListOf()

    internal fun action(block: ManagedWorkbookAction) {
        actions.add(block)
    }

    suspend fun build(): ManagedWorkbookResult {
        val path = path ?: return ManagedWorkbookResult.Failure(IllegalArgumentException("Path must be specified"))

        return when (val result = ManagedWorkbook.open(path)) {
            is ManagedWorkbookResult.Success -> {
                result.workbook.use { workbook ->
                    actions.forEach { action ->
                        workbook.action()
                    }
                }
                result
            }

            is ManagedWorkbookResult.Failure -> {
                result
            }
        }
    }
}