package com.khan366kos.domain.polynom.models

@JvmInline
value class MaxValue(val value: String) {

    constructor(value: Long) : this(value.toString())

    companion object {
        val NONE = MaxValue("")
    }

    fun asLong(): Long = value.toLong()
}