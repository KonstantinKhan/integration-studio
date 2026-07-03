package com.khan366kos.domain.models.business.elementGroup.simple

@JvmInline
value class ElementGroupName(val value: String) {
    fun asString() = value

    companion object {
        val NONE = ElementGroupName("")
    }
}