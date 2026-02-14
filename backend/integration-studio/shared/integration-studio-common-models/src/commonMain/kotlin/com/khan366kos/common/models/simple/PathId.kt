package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class PathId(private val value: String) {
    companion object {
        val NONE = PathId("")
    }

    fun asString() = value
}