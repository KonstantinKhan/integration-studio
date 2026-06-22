package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParentGroup(
    @SerialName("parentGroup")
    val parentGroup: IdentifiableObjectTransport
)
