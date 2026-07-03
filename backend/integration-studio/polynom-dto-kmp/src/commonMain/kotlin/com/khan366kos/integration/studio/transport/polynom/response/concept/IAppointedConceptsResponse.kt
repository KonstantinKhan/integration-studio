package com.khan366kos.integration.studio.transport.polynom.response.concept

import com.khan366kos.integration.studio.transport.polynom.models.concept.IAppointedConcept
import kotlinx.serialization.Serializable

@Serializable
data class IAppointedConceptsResponse(
    val isReverseOrder: Boolean? = null,
    val appointedConcepts: List<IAppointedConcept>? = null
)