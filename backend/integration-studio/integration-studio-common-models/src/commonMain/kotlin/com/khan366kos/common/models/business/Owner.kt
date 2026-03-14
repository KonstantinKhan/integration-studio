package com.khan366kos.common.models.business

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    @SerialName("owner")
    val owner: Identifier
)
