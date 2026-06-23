package com.khan366kos.integration.studio.transport.polynom.models

import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumDoublePropertyValue
import com.khan366kos.integration.studio.transport.polynom.models.definitions.IEnumIntPropertyValue
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
import kotlinx.serialization.Serializable

@Serializable
data class IAblePropertyValues(
    val doubleProperties: List<IDoublePropertyValue> = emptyList(),
    val stringProperties: List<IStringPropertyValue> = emptyList(),
    val booleanProperties: List<IBooleanPropertyValue> = emptyList(),
    val colorProperties: List<IColorPropertyValue> = emptyList(),
    val opticProperties: List<IOpticPropertyValue> = emptyList(),
    val dateTimeProperties: List<IDateTimePropertyValue> = emptyList(),
    val imageProperties: List<IImagePropertyValue> = emptyList(),
    val rtfProperties: List<IRtfPropertyValue> = emptyList(),
    val enumProperties: List<IEnumPropertyValue> = emptyList(),
    val setProperties: List<ISetPropertyValue> = emptyList(),
    val integerProperties: List<IIntegerPropertyValue> = emptyList(),
    val binaryProperties: List<IBinaryPropertyValue> = emptyList(),
    val guidProperties: List<IGuidPropertyValue> = emptyList(),
    val enumBoolProperties: List<IEnumBoolPropertyValue> = emptyList(),
    val enumDoubleProperties: List<IEnumDoublePropertyValue> = emptyList(),
    val enumIntProperties: List<IEnumIntPropertyValue> = emptyList(),
    val enumStringProperties: List<IEnumStringItem> = emptyList(),
    val tableProperties: List<ITablePropertyValue>? = null
)
