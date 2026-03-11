package com.khan366kos.common.models.values

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Values(
    val doubleProperties: List<DoublePropertyValue> = emptyList(),
    val stringProperties: List<StringPropertyValues> = emptyList(),
    val booleanProperties: List<BooleanPropertyValues>? = null,
    val colorProperties: List<ColorPropertyValues>? = null,
    val opticProperties: List<OpticPropertyValues>? = null,
    val dateTimeProperties: List<DateTimePropertyValue> = emptyList(),
    val imageProperties: List<ImagePropertyValues> = emptyList(),
    val rtfProperties: List<RtfPropertyValues>? = null,
    val enumProperties: List<EnumPropertyValues> = emptyList(),
    val setProperties: List<SetPropertyValues>? = null,
    val integerProperties: List<IntegerPropertyValues>? = null,
    val binaryProperties: List<BinaryPropertyValues>? = null,
    val guidProperties: List<GuidPropertyValues>? = null,
    val enumBoolProperties: List<EnumBoolPropertyValues>? = null,
    val enumDoubleProperties: List<EnumDoublePropertyValues>? = null,
    val enumIntProperties: List<EnumIntPropertyValues>? = null,
    val enumStringProperties: List<EnumStringPropertyValues>? = null,
    val tableProperties: List<TablePropertyValues>? = null
)

@Serializable
data class DoublePropertyValue(
    val value: DoublePropertyValues,
    val id: String,
    val definition: Identifier,
    val isNull: Boolean,
    val writeAccess: WriteAccess,
    val objectId: ObjectId,
    val typeId: TypeId
)

@Serializable
data class DoublePropertyValues(
    val objectId: ObjectId? = null,
    val typeId: TypeId? = null,
    val mode: Int,
    val value: Double,
    val minValue: Double,
    val maxValue: Double,
    val lowerTolerance: Double,
    val upperTolerance: Double,
    val measureUnit: Identifier? = null,
)

@Serializable
data class StringPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: String,
    val id: String,
    val definition: Identifier,
    val isNull: Boolean,
    val writeAccess: WriteAccess,
)

@Serializable
data class BooleanPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: Boolean
)

@Serializable
data class ColorPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int
)

@Serializable
data class OpticPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val ambient: Double,
    val diffuse: Double,
    val emission: Double,
    val shininess: Double,
    val specularity: Double,
    val transparency: Double
)

@Serializable
data class DateTimePropertyValue(
    val value: DateTimePropertyValues,
    val id: String,
    val definition: Identifier,
    val isNull: Boolean,
    val writeAccess: WriteAccess,
    val objectId: ObjectId,
    val typeId: TypeId
)

@Serializable
data class DateTimePropertyValues(
    val objectId: ObjectId? = null,
    val typeId: TypeId? = null,
    val value: String,
    val useTime: Boolean,
    val id: String? = null,
    val definition: Identifier? = null,
    val isNull: Boolean? = null,
    val writeAccess: WriteAccess? = null,
)

@Serializable
data class ImagePropertyValues(
    val body: String,
    val objectId: ObjectId,
    val typeId: TypeId,
    val isEmpty: Boolean? = null,
    val value: String,
    val id: String,
    val definition: Identifier,
    val isNull: Boolean,
    val writeAccess: WriteAccess,
)

@Serializable
data class RtfPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: String
)

@Serializable
data class EnumPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: String,
    val id: String,
    val definition: Identifier,
    val isNull: Boolean,
    val writeAccess: WriteAccess,
)

@Serializable
data class SetPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val values: List<String>
)

@Serializable
data class IntegerPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: Int
)

@Serializable
data class BinaryPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val isEmpty: Boolean,
    val value: String
)

@Serializable
data class GuidPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: String // Using string representation of UUID
)

@Serializable
data class EnumBoolPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: EnumItemValue<Boolean>
)

@Serializable
data class EnumDoublePropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: EnumItemValue<Double>
)

@Serializable
data class EnumIntPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: EnumItemValue<Int>
)

@Serializable
data class EnumStringPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: EnumItemValue<String>
)

@Serializable
data class TablePropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val columns: List<TableColumnValue>,
    val rows: List<TableRowValue>
)

@Serializable
data class EnumItemValue<T>(
    val description: String,
    val value: T,
    val position: Int
)

@Serializable
data class TableColumnValue(
    val objectId: ObjectId,
    val typeId: TypeId,
    val tablePropertyDefinitionColumn: Identifier,
    val width: Int
)

@Serializable
data class TableRowValue(
    val cellValues: List<Identifier>
)