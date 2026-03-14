package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserTransport(
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

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("firstName")
    val firstName: String? = null,

    @SerialName("patronymic")
    val patronymic: String? = null,

    @SerialName("lastName")
    val lastName: String? = null,

    @SerialName("isAdministrator")
    val isAdministrator: Boolean = false,

    @SerialName("email")
    val email: String? = null,

    @SerialName("fullName")
    val fullName: String? = null,

    @SerialName("im")
    val im: String? = null,

    @SerialName("login")
    val login: String? = null,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("hasPhoto")
    val hasPhoto: Boolean = false,

    @SerialName("additionalInfo")
    val additionalInfo: String? = null,

    @SerialName("web")
    val web: String? = null,

    @SerialName("roles")
    val roles: List<RoleTransport>? = null,

    @SerialName("positions")
    val positions: List<PositionTransport>? = null
)