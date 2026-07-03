package com.khan366kos.integration.studio.transport.polynom.models.catalog

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.Serializable

@Serializable
data class IElementCatalog(
    val name: String? = null,
    val iconCode: Int,
    val iconColor: Int? = null,
    val writeAccess: Boolean,
    val classId: Int? = null,
    val id: String? = null,
    val description: String? = null,
    val objectId: Int,
    val typeId: Int,
    val path: List<INamedObject>? = null,
    val count: Int,
    val reference: IIdentifiableObject,
    val isEntry: Boolean? = null,
)
