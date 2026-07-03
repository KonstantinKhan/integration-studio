package com.khan366kos.domain.requests

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.GroupId

data class ParentGroup(
    val objectId: GroupId,
    val name: ElementName
)