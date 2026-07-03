package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ISimpleConditionRequest(
    @SerialName("enabled")
    val enabled: Boolean,

    @SerialName("definition")
    val definition: IIdentifiableObject? = null,

    @SerialName("searchConditionTargetQualifier")
    val searchConditionTargetQualifier: IIdentifiableObject,

    @SerialName("operation")
    val operation: Int,

    @SerialName("options")
    val options: Int,

    @SerialName("value")
    val value: IIdentifiableObject
)
