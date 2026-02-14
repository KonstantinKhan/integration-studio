package com.khan366kos.common.requests

import com.khan366kos.common.models.simple.GroupId
import kotlinx.serialization.Serializable

@Serializable
data class IdRequest(
    val groupId: GroupId = GroupId.NONE
)
