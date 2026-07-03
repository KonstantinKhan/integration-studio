package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class PathId(private val value: String) {

    companion object {
        val NONE = PathId("")
    }

    fun asString() = value
}