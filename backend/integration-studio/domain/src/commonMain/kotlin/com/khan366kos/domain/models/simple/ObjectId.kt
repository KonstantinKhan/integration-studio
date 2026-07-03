package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class ObjectId(private val value: Int) {

    companion object {
        val NONE = ObjectId(-1)
    }

    fun asString() = value.toString()

    fun asInt() = value
}