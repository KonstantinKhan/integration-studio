package com.khan366kos.integration.studio.transport.polynom.request

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ITablePropertyRowRequest(
    @SerialName("cellValues")
    val cellValues: List<IIdentifiableObject>? = null,
)
