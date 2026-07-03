package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class IconColor(private val value: Int) {

    companion object {
        val NONE = IconColor(0)
    }

    fun asString() = value.toString()
}