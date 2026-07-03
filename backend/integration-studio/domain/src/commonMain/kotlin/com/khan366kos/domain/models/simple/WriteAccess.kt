package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class WriteAccess(private val value: Boolean) {

    companion object {
        val NONE = WriteAccess(false)
    }

    fun asBoolean() = value
}