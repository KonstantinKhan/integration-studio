package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElementCatalogTransport(
    @SerialName("name")
    val name: String? = null,

    @SerialName("iconCode")
    val iconCode: Int = 0,

    @SerialName("iconColor")
    val iconColor: Int? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("classId")
    val classId: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("path")
    val path: List<NamedObjectTransport>? = null,

    @SerialName("count")
    val count: Int = 0,

    @SerialName("reference")
    val reference: IdentifiableObjectTransport,

    @SerialName("isEntry")
    val isEntry: Boolean? = null
)