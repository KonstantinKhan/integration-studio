package com.khan366kos.integration.studio.transport.polynom.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IAblePropertyValuesRequest(
    @SerialName("doubleProperties")
    val doubleProperties: List<IDoublePropertyValueRequest>? = null,

    @SerialName("stringProperties")
    val stringProperties: List<IStringPropertyValueRequest>? = null,

    @SerialName("booleanProperties")
    val booleanProperties: List<IBooleanPropertyValueRequest>? = null,

    @SerialName("colorProperties")
    val colorProperties: List<IColorPropertyValueRequest>? = null,

    @SerialName("opticProperties")
    val opticProperties: List<IOpticPropertyValueRequest>? = null,

    @SerialName("dateTimeProperties")
    val dateTimeProperties: List<IDateTimePropertyValueRequest>? = null,

    @SerialName("imageProperties")
    val imageProperties: List<IImagePropertyValueRequest>? = null,

    @SerialName("rtfProperties")
    val rtfProperties: List<IRtfPropertyValueRequest>? = null,

    @SerialName("enumProperties")
    val enumProperties: List<IEnumPropertyValueRequest>? = null,

    @SerialName("setProperties")
    val setProperties: List<ISetPropertyValueRequest>? = null,

    @SerialName("integerProperties")
    val integerProperties: List<IIntegerPropertyValueRequest>? = null,

    @SerialName("binaryProperties")
    val binaryProperties: List<IBinaryPropertyValueRequest>? = null,

    @SerialName("guidProperties")
    val guidProperties: List<IGuidPropertyValueRequest>? = null,

    @SerialName("enumBoolProperties")
    val enumBoolProperties: List<IEnumBoolPropertyValueRequest>? = null,

    @SerialName("enumDoubleProperties")
    val enumDoubleProperties: List<IEnumDoublePropertyValueRequest>? = null,

    @SerialName("enumIntProperties")
    val enumIntProperties: List<IEnumIntPropertyValueRequest>? = null,

    @SerialName("enumStringProperties")
    val enumStringProperties: List<IEnumStringPropertyValueRequest>? = null,

    @SerialName("tableProperties")
    val tableProperties: List<ITablePropertyValueRequest> = emptyList()
)
