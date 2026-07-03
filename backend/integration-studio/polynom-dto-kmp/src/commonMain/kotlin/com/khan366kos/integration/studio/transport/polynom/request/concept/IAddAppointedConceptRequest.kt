package com.khan366kos.integration.studio.transport.polynom.request.concept

import com.khan366kos.integration.studio.transport.polynom.request.IIdentifierRequest
import kotlinx.serialization.Serializable

@Serializable
data class IAddAppointedConceptRequest(
    val conceptAppointer: IIdentifierRequest,
    val concept: IIdentifierRequest
)
