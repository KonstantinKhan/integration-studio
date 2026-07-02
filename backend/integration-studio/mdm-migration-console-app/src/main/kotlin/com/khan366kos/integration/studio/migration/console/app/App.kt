package com.khan366kos.integration.studio.migration.console.app

import com.khan366kos.etl.excel.service.ExcelService
import com.khan366kos.integration.studio.logics.ClassifierTreeBuilder

const val FILE_PATH = "/Users/khan/Projects/Структура Классификатора.xlsx"

fun main() {
    val excelService = ExcelService()
    val groups = excelService.classifierGroups(FILE_PATH)
    val treeBuilder = ClassifierTreeBuilder()
    val classifierTree = treeBuilder.build(groups)
    treeBuilder.printTree(listOf(classifierTree))
}