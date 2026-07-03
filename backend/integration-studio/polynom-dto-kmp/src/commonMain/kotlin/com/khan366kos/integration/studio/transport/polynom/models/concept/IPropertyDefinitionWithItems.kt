package com.khan366kos.integration.studio.transport.polynom.models.concept

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.IMeasureUnit
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumDoubleItem
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumIntItem
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumPropertyDefinitionItem
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumStringItem
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.ISetPropertyDefinitionItem
import kotlinx.serialization.Serializable

@Serializable
data class IPropertyDefinitionWithItems(
    val name: String? = null,
    val code: String? = null,
    val absoluteCode: String? = null,
    val writeAccess: Boolean,
    val ownerBaseGroup: IIdentifiableObject? = null,
    val id: String? = null,
    val description: String? = null,
    val isSystemObject: Boolean,
    val objectId: Int,
    val typeId: Int,
    val type: Int,
    val ownerGroup: IIdentifiableObject? = null,
    val defaultPropertyValue: IIdentifiableObject? = null,
    val enumItems: List<IEnumPropertyDefinitionItem>? = null,
    val enumStringItems: List<IEnumStringItem>? = null,
    val enumIntItems: List<IEnumIntItem>? = null,
    val enumDoubleItems: List<IEnumDoubleItem>? = null,
    val setItems: List<ISetPropertyDefinitionItem>? = null,
    val measureUnits: IMeasureUnit? = null,
)
