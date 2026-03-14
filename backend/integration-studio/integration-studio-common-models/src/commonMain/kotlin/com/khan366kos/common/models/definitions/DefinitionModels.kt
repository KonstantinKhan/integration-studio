package com.khan366kos.common.models.definitions

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Definitions(
    val doubleProperties: List<DoubleProperty>,
    val stringProperties: List<StringProperty>,
    val booleanProperties: List<BooleanProperty>? = null,
    val colorProperties: List<ColorProperty>? = null,
    val opticProperties: List<OpticProperty>? = null,
    val dateTimeProperties: List<DateTimeProperty>,
    val imageProperties: List<ImageProperty>,
    val rtfProperties: List<RtfProperty>,
    val enumProperties: List<EnumProperty>,
    val setProperties: List<SetProperty>? = null,
    val integerProperties: List<IntegerProperty>? = null,
    val binaryProperties: List<BinaryProperty>? = null,
    val guidProperties: List<GuidProperty>? = null,
    val enumBoolProperties: List<EnumBoolProperty>? = null,
    val enumDoubleProperties: List<EnumDoubleProperty>? = null,
    val enumIntProperties: List<EnumIntProperty>? = null,
    val enumStringProperties: List<EnumStringProperty>? = null,
    val tableProperties: List<TableProperty>? = null,
)

@Serializable
data class DoubleProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int? = null,
    val description: String? = null,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val measureEntity: Identifier,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val type: Int,
    val writeAccess: WriteAccess,
)

@Serializable
data class StringProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int? = null,
    val description: String? = null,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val writeAccess: WriteAccess,
    val type: Int,
)

@Serializable
data class BooleanProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int? = null,
    val description: String,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val writeAccess: WriteAccess,
    val type: Int,
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
    val dataType: Int? = null,
    val description: String? = null,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val writeAccess: WriteAccess,
    val type: Int,
)

@Serializable
data class ImageProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int? = null,
    val description: String? = null,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val writeAccess: WriteAccess,
    val type: Int,
)

@Serializable
data class RtfProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int? = null,
    val description: String? = null,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val writeAccess: WriteAccess,
    val type: Int,
)

@Serializable
data class EnumProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val name: String,
    val dataType: Int? = null,
    val description: String? = null,
    val code: String,
    val isSystemObject: Boolean,
    val absoluteCode: String,
    val items: List<EnumItem>,
    val ownerGroup: Identifier,
    val ownerBaseGroup: Identifier,
    val writeAccess: WriteAccess,
    val type: Int,
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
    val value: String,
    val position: Int,
    val id: String,
    val writeAccess: WriteAccess,
    val typeId: Int,
    val objectId: ObjectId,
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