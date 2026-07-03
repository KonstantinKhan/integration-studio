package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class ElementName(private val value: String) {
    companion object {
        val NONE = ElementName("")
    }

    fun asString() = value
}