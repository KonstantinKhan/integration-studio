package com.khan366kos.etl.excel.service

import kotlinx.coroutines.runBlocking
import org.apache.poi.xssf.usermodel.XSSFWorkbook

const val filePath = "C:\\Users\\han\\Desktop\\Структура Классификатора.xlsx"
const val startRowIndex: Int = 5
const val minValueColumnIndex: Int = 2
const val maxValueColumnIndex: Int = 3
const val levelColumnIndex: Int = 4
const val nameColumnIndex: Int = 5


fun main(): Unit = runBlocking {
    val workbook = XSSFWorkbook(filePath)
    workbook.use { workbook ->
        val sheet = workbook.getSheetAt(0)
        for (rowIndex in startRowIndex until sheet.lastRowNum) {
            val minValue = sheet.getRow(rowIndex).getCell(minValueColumnIndex).stringCellValue.cleanLong()
            val maxValue = sheet.getRow(rowIndex).getCell(maxValueColumnIndex).stringCellValue.cleanLong()
            val level = sheet.getRow(rowIndex).getCell(levelColumnIndex).numericCellValue.toInt()
            val name = sheet.getRow(rowIndex).getCell(nameColumnIndex).stringCellValue.trim()
            println("$minValue - $maxValue - $level - $name")
        }
    }
}

fun String.cleanLong() = filter { it.isDigit() }.toLong()