package com.khan366kos.etl.excel.service

import com.khan366kos.etl.excel.service.mapper.toEtl
import com.khan366kos.common.excel.models.EtlWorkbook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

sealed class ManagedWorkbookResult {
    data class Success(
        internal val workbook: ManagedWorkbook,
        val etlWorkbook: EtlWorkbook,
    ) : ManagedWorkbookResult()
    data class Failure(val exception: Exception) : ManagedWorkbookResult()
}

class ManagedWorkbook private constructor(
    private val workbook: XSSFWorkbook,
    private val inputStream: InputStream,
) : AutoCloseable {

    override fun close() {
        inputStream.use {
            workbook.close()
        }
    }

    internal fun sheets(): List<Sheet> = (0 until workbook.numberOfSheets).map { sheetNumber ->
        workbook.getSheetAt(sheetNumber)
    }

    internal suspend fun etlSheets() = sheets().map { sheet -> sheet.toEtl() }

    companion object {
        internal suspend fun open(path: String): com.khan366kos.etl.excel.service.ManagedWorkbookResult {
            val inputStream: InputStream = withContext(Dispatchers.IO) {
                Files.newInputStream(Paths.get(path))
            }
            return try {
                val xssfWorkbook = XSSFWorkbook(inputStream)
                val managedWorkbook = com.khan366kos.etl.excel.service.ManagedWorkbook(xssfWorkbook, inputStream)
                val etlWorkbook = xssfWorkbook.toEtl()

                ManagedWorkbookResult.Success(
                    workbook = managedWorkbook,
                    etlWorkbook = etlWorkbook
                )
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    inputStream.close()
                }
                ManagedWorkbookResult.Failure(e)
            }
        }
    }
}