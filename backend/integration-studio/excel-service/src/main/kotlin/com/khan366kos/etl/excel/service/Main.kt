package com.khan366kos.etl.excel.service

import com.khan366kos.etl.excel.service.dsl.function.useManagedWorkbook
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val filePath = "/Users/khan/Projects/etl-assistant/backend/etl-excel-service/src/main/resources/Book.xlsx"

    val result = useManagedWorkbook {
        path = filePath
        action {
            println(etlSheets())
        }
    }


//    when (val result = ManagedWorkbook.open(path = filePath)) {
//        is ManagedWorkbookResult.Success -> {
//            val managedWorkbook = result.workbook
//
//            managedWorkbook.workbook().use { workbook ->
//                val sheets = (0 until workbook.numberOfSheets)
//                    .map { sheetNumber ->
//                        workbook.getSheetAt(sheetNumber)
//                    }
//
//                val headers = sheets.map { sheet ->
//                    Pair(
//                        sheet.sheetName,
//                        sheet.first().map { cell -> cell.stringCellValue })
//                }
//
//                println("Типы полей:")
//                headers.forEach {
//                    println(
//                        """
//                        Вкладка: "${it.first}":
//                    """.trimIndent()
//                    )
//                    it.second.forEach { title ->
//                        println(" - $title")
//                    }
//                }
//            }
//        }
//
//        is ManagedWorkbookResult.Failure -> {
//            println("Failed to open workbook: ${result.exception.message}")
//            result.exception.printStackTrace()
//        }
//    }
}