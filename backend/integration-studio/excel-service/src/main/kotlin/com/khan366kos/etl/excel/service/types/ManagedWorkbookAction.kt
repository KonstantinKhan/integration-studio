package com.khan366kos.etl.excel.service.types

import com.khan366kos.etl.excel.service.ManagedWorkbook

typealias ManagedWorkbookAction = suspend ManagedWorkbook.() -> Unit