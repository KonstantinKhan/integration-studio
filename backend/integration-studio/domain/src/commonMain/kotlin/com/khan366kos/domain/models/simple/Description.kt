package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class Description(private val value: String) {

    companion object {
        val NONE = Description("")
    }

    fun asString() = value
}