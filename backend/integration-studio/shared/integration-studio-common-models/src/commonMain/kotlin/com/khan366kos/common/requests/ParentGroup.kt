package com.khan366kos.common.requests

import com.khan366kos.common.models.simple.ElementName
import com.khan366kos.common.models.simple.GroupId
import kotlinx.serialization.Serializable

@Serializable
data class ParentGroup(
    val objectId: GroupId,
    val name: ElementName
)