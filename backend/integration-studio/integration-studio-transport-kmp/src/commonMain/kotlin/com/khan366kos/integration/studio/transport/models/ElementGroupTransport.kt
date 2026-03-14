package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElementGroupTransport(
    @SerialName("name")
    val name: String? = null,

    @SerialName("iconCode")
    val iconCode: Int = 0,

    @SerialName("iconColor")
    val iconColor: Int? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("description")
    val description: String? = null,

    @SerialName("applicability")
    val applicability: Int,

    @SerialName("id")
    val id: String? = null,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("path")
    val path: List<NamedObjectTransport>? = null,

    @SerialName("parentCatalog")
    val parentCatalog: IdentifiableObjectTransport? = null,

    @SerialName("parentGroup")
    val parentGroup: IdentifiableObjectTransport? = null,

    @SerialName("hasObjects")
    val hasObjects: Boolean = false,

    @SerialName("count")
    val count: Int = 0,

    @SerialName("isEntry")
    val isEntry: Boolean? = null,

    @SerialName("classId")
    val classId: String? = null,

    @SerialName("isAllPartSizesTab")
    val isAllPartSizesTab: Boolean? = null
)