package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class ObjectId(private val value: Int) {
    companion object {
        val NONE = ObjectId(-1)
    }

    fun asString() = value.toString()
}