package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class ReferenceId(private val value: String) {
    companion object {
        val NONE = ReferenceId("")
    }

    fun asString() = value
}