package com.khan366kos.integration.studio.transport.polynom.models.definitions

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumStringItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IEnumStringPropertyDefinition(
    @SerialName("name")
    val name: String? = null,

    @SerialName("code")
    val code: String? = null,

    @SerialName("absoluteCode")
    val absoluteCode: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("ownerBaseGroup")
    val ownerBaseGroup: IIdentifiableObject,

    @SerialName("id")
    val id: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("isSystemObject")
    val isSystemObject: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("type")
    val type: Int,

    @SerialName("ownerGroup")
    val ownerGroup: IIdentifiableObject,

    @SerialName("defaultPropertyValue")
    val defaultPropertyValue: IIdentifiableObject,

    @SerialName("items")
    val items: List<IEnumStringItem>? = null,
)
