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
    ) : PropertyValue()
}

@Serializable
data class PropertyResult(
    val name: String,
    val value: PropertyValueSimple,
    val typeId: Int,
    val objectId: Int
)

@Serializable
sealed class PropertyValueSimple {

    @Serializable
    @SerialName("unknown")
    data class UnknownValSimple(
        val data: String = "Unknown",
    ) : PropertyValueSimple()

    @Serializable
    @SerialName("string")
    data class StringValSimple(
        val data: String,
    ) : PropertyValueSimple()

    @Serializable
    @SerialName("dateTime")
    data class DateTimeValSimple(
        val data: String,
    ) : PropertyValueSimple()

    @Serializable
    @SerialName("enum")
    data class EnumValSimple(
        val data: String,
    ) : PropertyValueSimple()

    @Serializable
    @SerialName("setVal")
    data class SetValSimple(
        val data: String,
    ) : PropertyValueSimple()

    @Serializable
    @SerialName("boolean")
    data class BooleanValSimple(
        val data: Boolean,
    ) : PropertyValueSimple()

}

fun PropertyValue.toSimple() = when (this) {
    is PropertyValue.StringVal -> PropertyValueSimple.StringValSimple(value)
    is PropertyValue.DateTimeVal -> PropertyValueSimple.DateTimeValSimple(value)
    is PropertyValue.EnumVal -> PropertyValueSimple.EnumValSimple(value)
    is PropertyValue.BooleanVal -> PropertyValueSimple.BooleanValSimple(value)
    is PropertyValue.SetVal -> PropertyValueSimple.SetValSimple(value)
    is PropertyValue.UnknownVal -> PropertyValueSimple.UnknownValSimple(value)
}
