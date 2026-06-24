package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySearchRequest(
    @SerialName("ownerScope")
    val ownerScope: IIdentifiableObject,

    @SerialName("condition")
    val condition: IComplexConditionRequest,

    @SerialName("values")
    val values: IAblePropertyValuesRequest,

    @SerialName("pageNumber")
    val pageNumber: Int,

    @SerialName("pageSize")
    val pageSize: Int,
)
