package com.khan366kos.integration.studio.transport.polynom.models.group

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.Serializable

@Serializable
data class IElementGroup(
    val name: String? = null,
    val iconCode: Int? = null,
    val iconColor: Int? = null,
    val writeAccess: Boolean? = null,
    val description: String? = null,
    val applicability: Int? = null,
    val id: String? = null,
    val objectId: Int,
    val typeId: Int,
    val path: List<INamedObject>? = null,
    val parentCatalog: IIdentifiableObject? = null,
    val parentGroup: IIdentifiableObject? = null,
    val hasObjects: Boolean,
    val count: Int,
    val createDeleteAccess: Boolean,
    val isEntry: Boolean? = null,
    val classId: String? = null,
    val isAllPartSizesTab: Boolean? = null,
)
