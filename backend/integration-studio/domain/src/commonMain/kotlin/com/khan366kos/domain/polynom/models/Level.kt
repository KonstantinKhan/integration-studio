package com.khan366kos.domain.polynom.models

@JvmInline
value class Level(val value: String) {

    constructor(value: Int) : this(value.toString())

    companion object {
        val NONE = Level("")
    }

    fun asInt(): Int = value.toInt()
}