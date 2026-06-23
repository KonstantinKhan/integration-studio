package com.khan366kos.integration.studio.transport.polynom.response

import com.khan366kos.integration.studio.transport.models.AppointedConceptDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointedConceptsDto(
    @SerialName("isReverseOrder")
    val isReverseOrder: Boolean,
    @SerialName("appointedConcepts")
    val appointedConcepts: List<AppointedConceptDto>? = null
)
