package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentCatalogTransport(
    @SerialName("name")
    val name: String?,
    
    @SerialName("iconCode")
    val iconCode: Int,
    
    @SerialName("iconColor")
    val iconColor: Int?,
    
    @SerialName("writeAccess")
    val writeAccess: Boolean,
    
    @SerialName("classId")
    val classId: String? = null,
    
    @SerialName("id")
    val id: String?,
    
    @SerialName("objectId")
    val objectId: Int,
    
    @SerialName("typeId")
    val typeId: Int,
    
    @SerialName("path")
    val path: List<NamedObjectTransport>?,
    
    @SerialName("count")
    val count: Int,
    
    @SerialName("reference")
    val reference: IdentifiableObjectTransport,
    
    @SerialName("isEntry")
    val isEntry: Boolean? = null
)