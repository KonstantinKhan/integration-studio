package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.Serializable

@Serializable
data class IClassificationNodeChildrenRequest(
    val pageNumber: Int,
    val pageSize: Int,
    val filterString: String? = null,
    val filterOptions: Int = 1,
    val parentNodeObject: IIdentifiableObject,
    val startIndex: Int = 0,
    val endIndex: Int = 0,
)
