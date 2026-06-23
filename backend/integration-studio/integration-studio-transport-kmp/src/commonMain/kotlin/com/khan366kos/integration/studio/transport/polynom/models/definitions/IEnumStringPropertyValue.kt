package com.khan366kos.integration.studio.transport.polynom.models.definitions

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.definitions.items.IEnumStringItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IEnumStringPropertyValue(
    @SerialName("id")
    val id: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("definition")
    val definition: IIdentifiableObject,

    @SerialName("value")
    val value: IEnumStringItem
)
