package com.khan366kos.common.models.business

import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId
import com.khan366kos.common.models.simple.WriteAccess
import kotlinx.serialization.Serializable

@Serializable
data class PropertyOwner(
    val id: String,
    val properties: List<Property>,
    val writeAccess: WriteAccess,
    val objectId: ObjectId,
    val typeId: TypeId
)
