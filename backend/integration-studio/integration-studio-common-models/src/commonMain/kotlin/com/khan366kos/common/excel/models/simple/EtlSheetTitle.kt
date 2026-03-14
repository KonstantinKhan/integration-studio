package com.khan366kos.common.excel.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class EtlSheetTitle(private val value: String) {

    fun asString() = value

    companion object {
        val NONE = EtlSheetTitle("")
    }
}