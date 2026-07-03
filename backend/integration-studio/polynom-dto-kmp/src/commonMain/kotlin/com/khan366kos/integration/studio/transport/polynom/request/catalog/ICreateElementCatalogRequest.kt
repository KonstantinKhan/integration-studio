package com.khan366kos.integration.studio.transport.polynom.request.catalog

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.Serializable

@Serializable
data class ICreateElementCatalogRequest(
    val reference: IIdentifiableObject,
    val name: String
)
