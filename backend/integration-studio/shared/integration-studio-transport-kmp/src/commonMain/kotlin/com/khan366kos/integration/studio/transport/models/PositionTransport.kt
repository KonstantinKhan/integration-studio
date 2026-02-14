package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PositionTransport(
    @SerialName("description")
    val description: String? = null,

    @SerialName("code")
    val code: String? = null,

    @SerialName("externalId")
    val externalId: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int
)