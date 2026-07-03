package com.khan366kos.domain.polynom.models

@JvmInline
value class MinValue(val value: String) {

    constructor(value: Long) : this(value.toString())

    companion object {
        val NONE = MinValue("")
    }

    fun asLong(): Long = value.toLong()
}