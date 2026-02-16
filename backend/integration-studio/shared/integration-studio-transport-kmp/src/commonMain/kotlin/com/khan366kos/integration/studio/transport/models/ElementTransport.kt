package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElementTransport(
    @SerialName("name")
    val name: String,
    @SerialName("iconCode")
    val iconCode: Int,
    @SerialName("iconColor")
    val iconColor: Int? = null,
    @SerialName("writeAccess")
    val writeAccess: Boolean,
    @SerialName("applicability")
    val applicability: Int,
    @SerialName("id")
    val id: String? = null,
    @SerialName("objectId")
    val objectId: Int,
    @SerialName("typeId")
    val typeId: Int,
    @SerialName("path")
    val path: List<NamedObjectTransport>? = null,
    @SerialName("ownerGroup")
    val ownerGroup: IdentifiableObjectTransport? = null,
    @SerialName("isMaterial")
    val isMaterial: Boolean,
    @SerialName("isAssortmentInstancesOwner")
    val isAssortmentInstancesOwner: Boolean,
) {
}