package com.khan366kos.integration.studio.transport.polynom.models

import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumDoublePropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumIntPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumStringPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumStringItem
import com.khan366kos.integration.studio.transport.polynom.models.properties.IBinaryPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IBooleanPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IColorPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IDateTimePropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IDoublePropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IEnumBoolPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IEnumPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IGuidPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IImagePropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IIntegerPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IOpticPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IRtfPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.ISetPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.IStringPropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.properties.ITablePropertyValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IAblePropertyValues(
    @SerialName("doubleProperties")
    val doubleProperties: List<IDoublePropertyValue>? = null,

    @SerialName("stringProperties")
    val stringProperties: List<IStringPropertyValue>? = null,

    @SerialName("booleanProperties")
    val booleanProperties: List<IBooleanPropertyValue>? = null,

    @SerialName("colorProperties")
    val colorProperties: List<IColorPropertyValue>? = null,

    @SerialName("opticProperties")
    val opticProperties: List<IOpticPropertyValue>? = null,

    @SerialName("dateTimeProperties")
    val dateTimeProperties: List<IDateTimePropertyValue>? = null,

    @SerialName("imageProperties")
    val imageProperties: List<IImagePropertyValue>? = null,

    @SerialName("rtfProperties")
    val rtfProperties: List<IRtfPropertyValue>? = null,

    @SerialName("enumProperties")
    val enumProperties: List<IEnumPropertyValue>? = null,

    @SerialName("setProperties")
    val setProperties: List<ISetPropertyValue>? = null,

    @SerialName("integerProperties")
    val integerProperties: List<IIntegerPropertyValue>? = null,

    @SerialName("binaryProperties")
    val binaryProperties: List<IBinaryPropertyValue>? = null,

    @SerialName("guidProperties")
    val guidProperties: List<IGuidPropertyValue>? = null,

    @SerialName("enumBoolProperties")
    val enumBoolProperties: List<IEnumBoolPropertyValue>? = null,

    @SerialName("enumDoubleProperties")
    val enumDoubleProperties: List<IEnumDoublePropertyValue>? = null,

    @SerialName("enumIntProperties")
    val enumIntProperties: List<IEnumIntPropertyValue>? = null,

    @SerialName("enumStringProperties")
    val enumStringProperties: List<IEnumStringPropertyValue>? = null,

    @SerialName("tableProperties")
    val tableProperties: List<ITablePropertyValue>? = null
)
