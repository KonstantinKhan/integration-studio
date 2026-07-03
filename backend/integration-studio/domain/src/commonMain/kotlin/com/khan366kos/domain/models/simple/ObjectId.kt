package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class ObjectId(private val value: Int) {

    companion object {
        val NONE = ObjectId(-1)
    }

    fun asString() = value.toString()

    fun asInt() = value
}