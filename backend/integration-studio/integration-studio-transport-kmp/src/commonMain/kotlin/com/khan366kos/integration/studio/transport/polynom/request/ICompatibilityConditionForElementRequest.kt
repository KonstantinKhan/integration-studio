package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ICompatibilityConditionForElementRequest(
    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("element")
    val element: IIdentifiableObject,

    @SerialName("linkDefinitionEnd")
    val linkDefinitionEnd: IIdentifiableObject,

    @SerialName("enabledComparingConditionDefinitions")
    val enabledComparingConditionDefinitions: List<IIdentifiableObject>? = null,
)
