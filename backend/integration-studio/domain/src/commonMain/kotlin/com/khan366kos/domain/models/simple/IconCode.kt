package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class IconCode(private val value: Int) {

    companion object {
        val NONE = IconCode(0)
    }

    fun asString() = value.toString()
}