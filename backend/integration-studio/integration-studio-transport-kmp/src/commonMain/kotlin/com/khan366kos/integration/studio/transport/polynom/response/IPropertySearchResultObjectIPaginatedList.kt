package com.khan366kos.integration.studio.transport.polynom.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySearchResultObjectIPaginatedList(
    @SerialName("pageNumber")
    val pageNumber: Int,

    @SerialName("pageSize")
    val pageSize: Int,

    @SerialName("totalPages")
    val totalPages: Int,

    @SerialName("totalCount")
    val totalCount: Int,

    @SerialName("hasPreviousPage")
    val hasPreviousPage: Boolean,

    @SerialName("hasNextPage")
    val hasNextPage: Boolean,

    @SerialName("items")
    val items: List<IPropertySearchResultObject>? = null,
)
