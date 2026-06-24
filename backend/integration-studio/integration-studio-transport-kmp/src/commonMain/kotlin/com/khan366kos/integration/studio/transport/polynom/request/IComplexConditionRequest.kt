package com.khan366kos.integration.studio.transport.polynom.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IComplexConditionRequest(
    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("intersectionType")
    val intersectionType: Int,

    @SerialName("simpleConditions")
    val simpleConditions: List<ISimpleConditionRequest>? = null,

    @SerialName("complexConditions")
    val complexConditions: List<IComplexConditionRequest>? = null,

    @SerialName("elementConditions")
    val elementConditions: List<ICompatibilityConditionForElementRequest>? = null,

    @SerialName("propValueConditions")
    val propValueConditions: List<ICompatibilityConditionForPropValueRequest>? = null,
)
