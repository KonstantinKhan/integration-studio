package com.khan366kos.integration.studio.bff.transport.request

import com.khan366kos.integration.studio.bff.transport.IdentifierBffDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ElementFromPeriodRequestBffDto(
    val scope: IdentifierBffDto,
    val from: LocalDateTime,
    val to : LocalDateTime,
) {
}