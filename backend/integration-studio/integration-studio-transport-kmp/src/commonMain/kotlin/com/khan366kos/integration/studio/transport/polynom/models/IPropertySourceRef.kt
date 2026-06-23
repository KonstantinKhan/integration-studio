package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySourceRef(
    @SerialName("absoluteCode")
    val absoluteCode: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("isLinked")
    val isLinked: Boolean,

    @SerialName("definition")
    val definition: IIdentifiableObject,

    @SerialName("ownerContract")
    val ownerContract: IIdentifiableObject,

    @SerialName("linkDefinitionEnd")
    val linkDefinitionEnd: IIdentifiableObject,

    @SerialName("linkedConceptPropertySource")
    val linkedConceptPropertySource: IIdentifiableObject,
)
