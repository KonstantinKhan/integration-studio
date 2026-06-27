package com.khan366kos.integration.studio.transport.polynom.response

import com.khan366kos.integration.studio.transport.polynom.models.IClassificationTreeNode
import kotlinx.serialization.Serializable

@Serializable
data class IClassificationTreeNodeIPaginatedList(
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalCount: Int,
    val hasPreviousPage: Boolean,
    val hasNextPage: Boolean,
    val items: List<IClassificationTreeNode>
)
