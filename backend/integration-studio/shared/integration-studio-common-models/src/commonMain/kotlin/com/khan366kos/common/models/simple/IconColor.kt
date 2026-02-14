package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class IconColor(private val value: Int) {
    companion object {
        val NONE = IconColor(0)
    }

    fun asString() = value.toString()
}