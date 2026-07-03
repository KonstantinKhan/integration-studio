package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class WriteAccess(private val value: Boolean) {

    companion object {
        val NONE = WriteAccess(false)
    }

    fun asBoolean() = value
}