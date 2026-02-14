package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class Description(private val value: String) {
    companion object {
        val NONE = Description("")
    }

    fun asString() = value
}