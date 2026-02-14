package com.khan366kos.common.models.definitions

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Definitions(
    val doubleProperties: List<DoubleProperty>,
    val stringProperties: List<StringProperty>,
    val booleanProperties: List<BooleanProperty>?,
    val colorProperties: List<ColorProperty>?,
    val opticProperties: List<OpticProperty>?,
    val dateTimeProperties: List<DateTimeProperty>,
    val imageProperties: List<ImageProperty>,
    val rtfProperties: List<RtfProperty>,
    val enumProperties: List<EnumProperty>,
    val setProperties: List<SetProperty>?,
    val integerProperties: List<IntegerProperty>?,
    val binaryProperties: List<BinaryProperty>?,
    val guidProperties: List<GuidProperty>?,
    val enumBoolProperties: List<EnumBoolProperty>?,
    val enumDoubleProperties: List<EnumDoubleProperty>?,
    val enumIntProperties: List<EnumIntProperty>?,
    val enumStringProperties: List<EnumStringProperty>?,
    val tableProperties: List<TableProperty>?
)

@Serializable
data class DoubleProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String?,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val measureEntity: Identifier
)

@Serializable
data class StringProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String?,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class BooleanProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class ColorProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class OpticProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class DateTimeProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String?,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class ImageProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String?,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class RtfProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String?,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class EnumProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String?,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<EnumItem>
)

@Serializable
data class SetProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<SetItem>
)

@Serializable
data class IntegerProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class BinaryProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class GuidProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String
)

@Serializable
data class EnumBoolProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<EnumBoolItem>
)

@Serializable
data class EnumDoubleProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<EnumDoubleItem>
)

@Serializable
data class EnumIntProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<EnumIntItem>
)

@Serializable
data class EnumStringProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<EnumStringItem>
)

@Serializable
data class TableProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val columns: List<TableColumn>
)

@Serializable
data class EnumItem(
    val description: String,
    val value: String,
    val position: Int
)

@Serializable
data class SetItem(
    val description: String,
    val value: String,
    val position: Int
)

@Serializable
data class EnumBoolItem(
    val description: String,
    val value: Boolean,
    val position: Int
)

@Serializable
data class EnumDoubleItem(
    val description: String,
    val value: Double,
    val position: Int
)

@Serializable
data class EnumIntItem(
    val description: String,
    val value: Int,
    val position: Int
)

@Serializable
data class EnumStringItem(
    val description: String,
    val value: String,
    val position: Int
)

@Serializable
data class TableColumn(
    val objectId: ObjectId,
    val typeId: TypeId,
    val owner: Identifier,
    val dataType: Int,
    val definition: Identifier,
    val defaultValue: Identifier,
    val absoluteCode: String,
    val isMandatory: Boolean,
    val isHidden: Boolean,
    val isSpecial: Boolean,
    val isUnique: Boolean,
    val isIndexable: Boolean,
    val isDynamic: Boolean,
    val isDisplayedForSelection: Boolean,
    val isSetBeforeApplying: Boolean,
    val isReadOnly: Boolean,
    val isDefaultIfEmpty: Boolean,
    val isNameManuallySet: Boolean,
    val position: Int,
    val isUniqueInTable: Boolean
)