package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferenceTransport(
    @SerialName("name")
    val name: String? = null,
    
    @SerialName("iconCode")
    val iconCode: Int,
    
    @SerialName("iconColor")
    val iconColor: Int? = null,
    
    @SerialName("writeAccess")
    val writeAccess: Boolean,
    
    @SerialName("id")
    val id: String? = null,
    
    @SerialName("description")
    val description: String? = null,
    
    @SerialName("objectId")
    val objectId: Int,
    
    @SerialName("typeId")
    val typeId: Int,
    
    @SerialName("path")
    val path: List<NamedObjectTransport>? = null,
    
    @SerialName("documentCatalog")
    val documentCatalog: DocumentCatalogTransport? = null,
    
    @SerialName("viewpointCatalog")
    val viewpointCatalog: ViewpointCatalogTransport? = null
)