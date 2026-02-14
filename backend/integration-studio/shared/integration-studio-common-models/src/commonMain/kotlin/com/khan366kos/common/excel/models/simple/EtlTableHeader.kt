package com.khan366kos.common.excel.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class EtlTableHeader(private val value: String) {

    fun asString() = value

    companion object {
        val NONE = EtlTableHeader("")
    }
}