package com.khan366kos.integration.studio.transport.polynom.models

import com.khan366kos.integration.studio.transport.polynom.models.definitions.IBinaryPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IBooleanPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IColorPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IDateTimePropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IDoublePropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumDoublePropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumIntPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumStringPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IGuidPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IImagePropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IIntegerPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IOpticPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IRtfPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.ISetPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IStringPropertyDefinition
import com.khan366kos.integration.studio.transport.polynom.models.definitions.ITablePropertyDefinition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IAblePropertyDefinitions(
    @SerialName("doubleProperties")
    val doubleProperties: List<IDoublePropertyDefinition>? = null,

    @SerialName("stringProperties")
    val stringProperties: List<IStringPropertyDefinition>? = null,

    @SerialName("booleanProperties")
    val booleanProperties: List<IBooleanPropertyDefinition>? = null,

    @SerialName("colorProperties")
    val colorProperties: List<IColorPropertyDefinition>? = null,

    @SerialName("opticProperties")
    val opticProperties: List<IOpticPropertyDefinition>? = null,

    @SerialName("dateTimeProperties")
    val dateTimeProperties: List<IDateTimePropertyDefinition>? = null,

    @SerialName("imageProperties")
    val imageProperties: List<IImagePropertyDefinition>? = null,

    @SerialName("rtfProperties")
    val rtfProperties: List<IRtfPropertyDefinition>? = null,

    @SerialName("enumProperties")
    val enumProperties: List<IEnumPropertyDefinition>? = null,

    @SerialName("setProperties")
    val setProperties: List<ISetPropertyDefinition>? = null,

    @SerialName("integerProperties")
    val integerProperties: List<IIntegerPropertyDefinition>? = null,

    @SerialName("binaryProperties")
    val binaryProperties: List<IBinaryPropertyDefinition>? = null,

    @SerialName("guidProperties")
    val guidProperties: List<IGuidPropertyDefinition>? = null,

    @SerialName("enumBoolProperties")
    val enumBoolProperties: List<IEnumPropertyDefinition>? = null,

    @SerialName("enumDoubleProperties")
    val enumDoubleProperties: List<IEnumDoublePropertyDefinition>? = null,

    @SerialName("enumIntProperties")
    val enumIntProperties: List<IEnumIntPropertyDefinition>? = null,

    @SerialName("enumStringProperties")
    val enumStringProperties: List<IEnumStringPropertyDefinition>? = null,

    @SerialName("tableProperties")
    val tableProperties: List<ITablePropertyDefinition>? = null,
)
