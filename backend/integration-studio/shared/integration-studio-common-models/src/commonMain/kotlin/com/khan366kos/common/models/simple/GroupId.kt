package com.khan366kos.common.models.simple

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class GroupId(val value: Int) {
    companion object {
        val NONE = GroupId(0)
    }

    fun asString() = value.toString()
}