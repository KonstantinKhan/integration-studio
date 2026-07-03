package com.khan366kos.integration.studio.transport.polynom.response.concept

import com.khan366kos.integration.studio.transport.polynom.models.concept.IConcept
import kotlinx.serialization.Serializable

@Serializable
data class IConceptPaginatedList(
    val pageNumber: Int,
    val pageSize: Int,
    val totalPage: Int? = null,
    val totalCount: Int,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val items: List<IConcept>? = null
)
