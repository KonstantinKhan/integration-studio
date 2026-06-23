package com.khan366kos.integration.studio.transport.models

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ViewpointCatalogTransport(
    @SerialName("name")
    val name: String? = null,

    @SerialName("iconCode")
    val iconCode: Int,

    @SerialName("iconColor")
    val iconColor: Int? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("classId")
    val classId: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("path")
    val path: List<NamedObjectDto>? = null,

    @SerialName("count")
    val count: Int,

    @SerialName("reference")
    val reference: IIdentifiableObject? = null,
)