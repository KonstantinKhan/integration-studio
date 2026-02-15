package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class NamePath(val value: String) {
    fun asString() = value

    companion object {
        val NONE = NamePath("")
    }
}