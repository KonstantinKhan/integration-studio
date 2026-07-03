package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(

    @SerialName("moduleName")
    val moduleName: String? = null,

    @SerialName("clientType")
    val clientType: Int = 0,

    @SerialName("storageId")
    val storageId: String? = null,

    @SerialName("login")
    val login: String? = null,
    @SerialName("password")
    val password: String? = null,
)