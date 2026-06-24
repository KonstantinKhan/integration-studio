package com.khan366kos.integration.studio.transport.polynom.models.properties

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IBooleanPropertyValue(
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

    @SerialName("isNull")
    val isNull: Boolean,

    @SerialName("value")
    val value: Boolean? = null
)
