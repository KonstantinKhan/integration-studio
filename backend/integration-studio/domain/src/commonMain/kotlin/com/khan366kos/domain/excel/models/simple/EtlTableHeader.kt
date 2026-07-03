package com.khan366kos.domain.excel.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class EtlTableHeader(private val value: String) {

    fun asString() = value

    companion object {
        val NONE = EtlTableHeader("")
    }
}