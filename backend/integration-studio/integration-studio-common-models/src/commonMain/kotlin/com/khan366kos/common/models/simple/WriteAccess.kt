package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class WriteAccess(private val value: Boolean) {
    companion object {
        val NONE = WriteAccess(false)
    }

    fun asBoolean() = value
}