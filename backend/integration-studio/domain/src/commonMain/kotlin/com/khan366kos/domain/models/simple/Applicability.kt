package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class Applicability(private val value: Int) {
    companion object {
        val NONE = Applicability(0)
    }

    fun asString() = value.toString()
}