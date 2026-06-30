package com.khan366kos.integration.studio.transport.polynom.response.search

import com.khan366kos.integration.studio.transport.polynom.response.IPropertySearchResultObject
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySearchResultObjectIPaginatedList(
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalCount: Int,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val items: List<IPropertySearchResultObject>? = null,
)