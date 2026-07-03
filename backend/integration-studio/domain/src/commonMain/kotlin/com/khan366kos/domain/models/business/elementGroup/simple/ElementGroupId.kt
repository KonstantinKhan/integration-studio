package com.khan366kos.domain.models.business.elementGroup.simple

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ElementGroupId(val value: String) {
    fun asString(): String = value

    companion object {
        val NONE = ElementGroupId("")
    }
}