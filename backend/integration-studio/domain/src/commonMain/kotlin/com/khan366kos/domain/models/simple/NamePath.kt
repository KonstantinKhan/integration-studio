package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class NamePath(val value: String) {

    companion object {
        val NONE = NamePath("")
    }

    fun asString() = value
}