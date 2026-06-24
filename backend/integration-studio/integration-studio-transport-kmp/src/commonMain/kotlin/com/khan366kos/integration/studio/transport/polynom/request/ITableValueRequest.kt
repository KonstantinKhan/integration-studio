package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ITableValueRequest(
    @SerialName("columns")
    val columns: List<IIdentifiableObject>? = null,

    @SerialName("rows")
    val rows: List<ITablePropertyRowRequest>? = null,
)
