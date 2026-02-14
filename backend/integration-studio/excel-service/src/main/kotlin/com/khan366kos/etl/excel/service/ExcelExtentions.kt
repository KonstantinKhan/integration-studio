package com.khan366kos.etl.excel.service

import kotlinx.coroutines.*
import org.apache.poi.ss.usermodel.Sheet
import kotlin.math.ceil
import kotlin.math.min

fun Sheet.headers(): List<String> = first().toList().map { cell -> cell.stringCellValue }

suspend fun Sheet.entriesSize(): Int = withContext(Dispatchers.IO) {
    val totalRows = this@entriesSize.lastRowNum + 1
    val chunkCount = when {
        totalRows > 4000 -> totalRows / 2000
        totalRows < 100 -> 1
        else -> 4
    }

    val chunkSize = if (totalRows > 0) ceil(totalRows.toDouble() / chunkCount).toInt() else 0

    val deferredResults = (0 until chunkCount).map { i ->
        async {
            val startRow = i * chunkSize
            val endRow = min(startRow + chunkSize, totalRows)

            var chunkCount = 0
            for (rowIndex in startRow until endRow) {
                val row = this@entriesSize.getRow(rowIndex)
                if (row != null && row.toList().isNotEmpty()) {
                    chunkCount++
                }
            }
            chunkCount
        }
    }

    val totalCount = deferredResults.awaitAll().sum()

    return@withContext totalCount - 1
}
