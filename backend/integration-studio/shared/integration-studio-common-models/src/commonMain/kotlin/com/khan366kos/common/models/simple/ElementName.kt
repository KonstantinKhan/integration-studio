package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class ElementName(private val value: String) {
    companion object {
        val NONE = ElementName("")
    }

    fun asString() = value
}