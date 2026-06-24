package com.khan366kos.integration.studio.transport.polynom.response

import com.khan366kos.integration.studio.transport.polynom.models.IClassifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySearchResultObject(
    @SerialName("name")
    val name: String? = null,

    @SerialName("iconCode")
    val iconCode: Int,

    @SerialName("iconColor")
    val iconColor: Int? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("path")
    val path: List<INamedObject>? = null,

    @SerialName("applicability")
    val applicability: Int,

    @SerialName("innerObjects")
    val innerObjects: List<IClassifiableObject>? = null,

    @SerialName("multiClassificationPaths")
    val multiClassificationPaths: List<INamedObject>? = null,

    @SerialName("tableRow")
    val tableRow: INamedObject? = null
)
