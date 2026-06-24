package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ICompatibilityConditionForPropValueRequest(
    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("linkDefinitionEnd")
    val linkDefinitionEnd: IIdentifiableObject,

    @SerialName("comparingConditions")
    val comparingConditions: List<IComparingConditionForPropValueRequest>? = null,
)
