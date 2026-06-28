package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.models.simple.WriteAccess
import kotlinx.serialization.Serializable

@Serializable
data class LinkedPropertyInfo(
    val writeAccess: WriteAccess,
    val objectId: ObjectId,
    val typeId: TypeId,
)
