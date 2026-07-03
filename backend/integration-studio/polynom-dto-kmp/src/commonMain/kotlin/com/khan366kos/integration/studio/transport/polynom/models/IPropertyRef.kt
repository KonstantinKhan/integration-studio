package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertyRef(
    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("type")
    val type: Int,

    @SerialName("isOwn")
    val isOwn: Boolean,

    @SerialName("isLinked")
    val isLinked: Boolean,

    @SerialName("contract")
    val contract: IIdentifiableObject,

    @SerialName("definition")
    val definition: IIdentifiableObject,

    @SerialName("contractPropertySource")
    val contractPropertySource: IIdentifiableObject? = null,

    @SerialName("linkedPropertyInfo")
    val linkedPropertyInfo: ILinkedPropertyInfoRef,

    @SerialName("value")
    val value: IIdentifiableObject? = null,

    @SerialName("evaluationPropertyInfo")
    val evaluationPropertyInfo: IEvaluationPropertyRef,
)
