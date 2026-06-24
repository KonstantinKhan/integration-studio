package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IComparingConditionForPropValueRequest(
    @SerialName("isEnabled")
    val isEnabled: Boolean,

    @SerialName("comparingConditionDefinition")
    val comparingConditionDefinition: IIdentifiableObject,

    @SerialName("propertyValue")
    val propertyValue: IIdentifiableObject
)
