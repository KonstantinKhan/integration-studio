package com.khan366kos.domain.requests

import com.khan366kos.domain.models.simple.GroupId
import kotlinx.serialization.Serializable

@Serializable
data class IdRequest(
    val groupId: GroupId = GroupId.NONE
)
