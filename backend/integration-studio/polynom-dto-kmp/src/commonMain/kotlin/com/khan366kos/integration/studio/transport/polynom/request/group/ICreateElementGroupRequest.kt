package com.khan366kos.integration.studio.transport.polynom.request.group

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.Serializable

@Serializable
data class ICreateElementGroupRequest(
    val parentGroup: IIdentifiableObject,
    val name: String? = null,
)
