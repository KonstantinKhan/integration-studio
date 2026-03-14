package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleTransport(
    @SerialName("description")
    val description: String? = null,

    @SerialName("code")
    val code: String? = null,

    @SerialName("externalId")
    val externalId: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("id")
    val id: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("isSystemObject")
    val isSystemObject: Boolean = false,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int
)