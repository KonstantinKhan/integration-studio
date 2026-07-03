package com.khan366kos.domain.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class GroupId(val value: Int) {

    companion object {
        val NONE = GroupId(0)
    }

    fun asString() = value.toString()
}