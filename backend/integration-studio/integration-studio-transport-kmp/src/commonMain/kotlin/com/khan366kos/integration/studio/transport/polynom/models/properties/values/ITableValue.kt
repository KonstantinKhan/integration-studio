package com.khan366kos.integration.studio.transport.polynom.models.properties.values

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ITableValue(
    @SerialName("columns")
    val columns: List<IIdentifiableObject>? = null,

    @SerialName("rows")
    val rows: List<ITablePropertyRow>? = null,
)