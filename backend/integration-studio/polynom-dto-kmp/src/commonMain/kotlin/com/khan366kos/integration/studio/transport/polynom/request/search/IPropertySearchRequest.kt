package com.khan366kos.integration.studio.transport.polynom.request.search

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.request.IAblePropertyValuesRequest
import com.khan366kos.integration.studio.transport.polynom.request.IComplexConditionRequest
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySearchRequest(
    val ownerScope: IIdentifiableObject,
    val condition: IComplexConditionRequest,
    val values: IAblePropertyValuesRequest,
    val pageNumber: Int,
    val pageSize: Int,
)