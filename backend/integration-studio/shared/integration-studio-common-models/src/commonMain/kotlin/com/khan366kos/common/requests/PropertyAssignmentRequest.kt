package com.khan366kos.common.requests

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.values.Values
import kotlinx.serialization.Serializable

@Serializable
data class PropertyAssignmentRequest(
    val values: Values,
    val properties: List<PropertyValueAssignment>,
    val propertyOwner: Identifier
)