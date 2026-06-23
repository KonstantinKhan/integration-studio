package com.khan366kos.integration.studio.transport.models

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
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
    val path: List<NamedObjectDto>? = null,

    @SerialName("parentCatalog")
    val parentCatalog: IIdentifiableObject? = null,

    @SerialName("parentGroup")
    val parentGroup: IIdentifiableObject? = null,

    @SerialName("hasObjects")
    val hasObjects: Boolean = false,

    @SerialName("count")
    val count: Int = 0,

    @SerialName("createDeleteAccess")
    val createDeleteAccess: Boolean,

    @SerialName("isEntry")
    val isEntry: Boolean? = null,

    @SerialName("classId")
    val classId: String? = null,

    @SerialName("isAllPartSizesTab")
    val isAllPartSizesTab: Boolean? = null
)