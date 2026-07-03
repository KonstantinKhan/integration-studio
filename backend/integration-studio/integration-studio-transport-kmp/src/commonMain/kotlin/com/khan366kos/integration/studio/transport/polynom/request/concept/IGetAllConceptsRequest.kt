package com.khan366kos.integration.studio.transport.polynom.request.concept

import kotlinx.serialization.Serializable

@Serializable
data class IGetAllConceptsRequest(
    val pageNumber: Int,
    val pageSize: Int,
    val filterString: String? = null,
)
