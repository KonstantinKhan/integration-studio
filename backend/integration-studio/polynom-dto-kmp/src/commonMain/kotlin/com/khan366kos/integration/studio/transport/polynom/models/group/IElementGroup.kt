package com.khan366kos.integration.studio.transport.polynom.models.group

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.Serializable

@Serializable
data class IElementGroup(
    val name: String? = null,
    val iconCode: Int,
    val iconColor: Int? = null,
    val writeAccess: Boolean,
    val description: String? = null,
    val applicability: Int,
    val id: String? = null,
    val objectId: Int,
    val typeId: Int,
    val path: List<INamedObject>? = null,
    val parentCatalog: IIdentifiableObject,
    val parentGroup: IIdentifiableObject,
    val hasObjects: Boolean,
    val count: Int,
    val createDeleteAccess: Boolean,
    val isEntry: Boolean? = null,
    val classId: String? = null,
    val isAllPartSizesTab: Boolean? = null,
)
