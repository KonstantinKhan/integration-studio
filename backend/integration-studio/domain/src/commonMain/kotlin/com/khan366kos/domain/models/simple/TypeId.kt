package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class TypeId(private val value: Int) {

    companion object {
        val NONE = TypeId(-1)
    }

    fun asString() = value.toString()

    fun asInt(): Int = value
}