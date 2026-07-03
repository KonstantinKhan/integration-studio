package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class ElementName(private val value: String) {
    companion object {
        val NONE = ElementName("")
    }

    fun asString() = value
}