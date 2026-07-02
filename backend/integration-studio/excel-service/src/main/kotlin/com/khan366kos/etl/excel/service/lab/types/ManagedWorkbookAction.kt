package com.khan366kos.etl.excel.service.lab.types

import com.khan366kos.etl.excel.service.lab.ManagedWorkbook

typealias ManagedWorkbookAction = suspend ManagedWorkbook.() -> Unit