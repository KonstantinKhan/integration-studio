package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.Serializable

@Serializable
data class IAccessControlObject(
    val writeAccess: Boolean,
    val objectId: Int,
    val typeId: Int
)
