package com.khan366kos.common.models

import java.time.LocalDateTime


sealed class PropertyValue {
    abstract val typeId: Int
    abstract val objectId: Int

    data class StringVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int
    ) : PropertyValue()

    data class DateTimeVal(
        val value: LocalDateTime,
        override val typeId: Int,
        override val objectId: Int,
    ) : PropertyValue()
}

data class PropertyResult(
    val name: String,
    val value: PropertyValue,
    val typeId: Int,
    val objectId: Int
)



