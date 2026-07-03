package com.khan366kos.domain.polynom

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class Property {
    abstract val typeId: Int
    abstract val objectId: Int

    @Serializable
    @SerialName("unknown")
    data class EmptyVal(
        val value: String = "empty",
        override val typeId: Int,
        override val objectId: Int
    ) : Property()

    @Serializable
    @SerialName("string")
    data class StringVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int
    ) : Property()

    @Serializable
    @SerialName("dateTime")
    data class DateTimeVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int,
    ) : Property()

    @Serializable
    @SerialName("enum")
    data class EnumVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int,
    ) : Property()

    @Serializable
    @SerialName("setVal")
    data class SetVal(
        val value: String,
        override val typeId: Int,
        override val objectId: Int,
    ) : Property()

    @Serializable
    @SerialName("boolean")
    data class BooleanVal(
        val value: Boolean,
        override val typeId: Int,
        override val objectId: Int,
    ) : Property()
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
    @SerialName("empty")
    data class EmptyValSimple(
        val data: String = "empty",
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

fun Property.toSimple() = when (this) {
    is Property.StringVal -> PropertyValueSimple.StringValSimple(value)
    is Property.DateTimeVal -> PropertyValueSimple.DateTimeValSimple(value)
    is Property.EnumVal -> PropertyValueSimple.EnumValSimple(value)
    is Property.BooleanVal -> PropertyValueSimple.BooleanValSimple(value)
    is Property.SetVal -> PropertyValueSimple.SetValSimple(value)
    is Property.EmptyVal -> PropertyValueSimple.EmptyValSimple(value)
}
