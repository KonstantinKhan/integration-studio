package com.khan366kos.common.models.values

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Values(
    val doubleProperties: List<DoublePropertyValues> = emptyList(),
    val stringProperties: List<StringPropertyValues> = emptyList(),
    val booleanProperties: List<BooleanPropertyValues> = emptyList(),
    val colorProperties: List<ColorPropertyValues> = emptyList(),
    val opticProperties: List<OpticPropertyValues> = emptyList(),
    val dateTimeProperties: List<DateTimePropertyValues> = emptyList(),
    val imageProperties: List<ImagePropertyValues> = emptyList(),
    val rtfProperties: List<RtfPropertyValues> = emptyList(),
    val enumProperties: List<EnumPropertyValues> = emptyList(),
    val setProperties: List<SetPropertyValues> = emptyList(),
    val integerProperties: List<IntegerPropertyValues> = emptyList(),
    val binaryProperties: List<BinaryPropertyValues> = emptyList(),
    val guidProperties: List<GuidPropertyValues> = emptyList(),
    val enumBoolProperties: List<EnumBoolPropertyValues> = emptyList(),
    val enumDoubleProperties: List<EnumDoublePropertyValues> = emptyList(),
    val enumIntProperties: List<EnumIntPropertyValues> = emptyList(),
    val enumStringProperties: List<EnumStringPropertyValues> = emptyList(),
    val tableProperties: List<TablePropertyValues> = emptyList()
)

@Serializable
data class DoublePropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val mode: Int,
    val value: Double,
    val minValue: Double,
    val maxValue: Double,
    val lowerTolerance: Double,
    val upperTolerance: Double,
    val measureUnit: Identifier
)

@Serializable
data class StringPropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: String
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
data class DateTimePropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val value: String, // Using string representation of date-time
    val useTime: Boolean
)

@Serializable
data class ImagePropertyValues(
    val objectId: ObjectId,
    val typeId: TypeId,
    val isEmpty: Boolean,
    val value: String
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
    val value: String
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