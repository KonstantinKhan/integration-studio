package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ITablePropertyRow(
    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("owner")
    val owner: IIdentifiableObject,

    @SerialName("owningValue")
    val owningValue: IIdentifiableObject,

    @SerialName("cellValues")
    val cellValues: List<IIdentifiableObject>? = null,

    @SerialName("position")
    val position: Int
)
