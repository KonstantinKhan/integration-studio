package com.khan366kos.common.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PropertyValue {
    abstract val typeId: Int
    abstract val objectId: Int

    @Serializable
    @SerialName("unknown")
    data class UnknownVal(
        val value: String = "unknown",
        override val typeId: Int,
        override val objectId: Int
    ) : PropertyValue()

    @Serializable
    @SerialName("string")
    data class StringVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int
    ) : PropertyValue()

    @Serializable
    @SerialName("dateTime")
    data class DateTimeVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int,
    ) : PropertyValue()

    @Serializable
    @SerialName("enum")
    data class EnumVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int,
    ) : PropertyValue()

    @Serializable
    @SerialName("setVal")
    data class SetVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int,
    ) : PropertyValue()

    @Serializable
    @SerialName("boolean")
    data class BooleanVal(
        val value: Boolean,
        override val typeId: Int,
        override val objectId: Int,
    ): PropertyValue()
}

@Serializable
data class PropertyResult(
    val name: String,
    val value: PropertyValue,
    val typeId: Int,
    val objectId: Int
)
