package com.khan366kos.etl.excel.service

import com.khan366kos.domain.classifier.ClassifierGroupExcel
import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.polynom.models.Level
import com.khan366kos.domain.polynom.models.MaxValue
import com.khan366kos.domain.polynom.models.MinValue
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class ExcelService(
    private val minValueColumnIndex: Int = 2,
    private val maxValueColumnIndex: Int = 3,
    private val levelColumnIndex: Int = 4,
    private val nameColumnIndex: Int = 5,
    private val codeDigitCount: Int = 18
) {
    fun classifierGroups(path: String): List<ClassifierGroupExcel> {
        val groups = mutableListOf<ClassifierGroupExcel>()
        val workbook = XSSFWorkbook(path)
        workbook.use { workbook ->
            val sheet = workbook.getSheetAt(0)
            for (rowIndex in 0..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val minCell = row.getCell(minValueColumnIndex) ?: continue
                val maxCell = row.getCell(maxValueColumnIndex) ?: continue
                val levelCell = row.getCell(levelColumnIndex) ?: continue
                val nameCell = row.getCell(nameColumnIndex) ?: continue
                if (minCell.cellType != CellType.STRING) continue
                val rawMin = minCell.stringCellValue
                if (rawMin.count { it.isDigit() } != codeDigitCount) continue
                val minValue = rawMin.cleanLong()
                val maxValue = maxCell.stringCellValue.cleanLong()
                val level = levelCell.numericCellValue.toInt()
                val name = nameCell.stringCellValue.trim()
                groups.add(
                    ClassifierGroupExcel(
                        MinValue(minValue),
                        MaxValue(maxValue),
                        Level(level),
                        ElementName(name)
                    )
                )
            }
        }
        return groups
    }

    private fun String.cleanLong() = filter { it.isDigit() }.toLong()
}