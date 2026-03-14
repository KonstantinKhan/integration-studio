package com.khan366kos.common.models.business.elementGroup.simple

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ElementGroupId(val value: String) {
    fun asString(): String = value

    companion object {
        val NONE = ElementGroupId("")
    }
}