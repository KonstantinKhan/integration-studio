package com.khan366kos.integration.studio.bff.transport.request

import com.khan366kos.integration.studio.bff.transport.models.IdentifierBffDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PolynomElementFromPeriodRequestBffDto(
    val scope: IdentifierBffDto,
    val from: LocalDateTime,
    val to: LocalDateTime,
)