package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Applicability(private val value: Int) {

    companion object {
        val NONE = Applicability(0)
    }

    fun asString() = value.toString()
}