package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Description(private val value: String) {

    companion object {
        val NONE = Description("")
    }

    fun asString() = value
}