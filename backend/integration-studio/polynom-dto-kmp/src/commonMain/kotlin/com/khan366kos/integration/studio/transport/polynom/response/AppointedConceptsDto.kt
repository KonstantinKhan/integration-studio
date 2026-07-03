package com.khan366kos.integration.studio.transport.polynom.response

import com.khan366kos.integration.studio.transport.models.AppointedConceptDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointedConceptsDto(
    val isReverseOrder: Boolean,
    val appointedConcepts: List<AppointedConceptDto>? = null
)
