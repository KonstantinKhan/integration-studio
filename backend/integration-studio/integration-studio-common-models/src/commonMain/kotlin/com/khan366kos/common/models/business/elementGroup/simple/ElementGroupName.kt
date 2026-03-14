package com.khan366kos.common.models.business.elementGroup.simple

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ElementGroupName(val value: String) {
    fun asString() = value

    companion object {
        val NONE = ElementGroupName("")
    }
}