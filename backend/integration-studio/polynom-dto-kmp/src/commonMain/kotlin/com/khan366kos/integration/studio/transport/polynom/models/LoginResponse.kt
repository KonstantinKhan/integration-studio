package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(

    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("token_type")
    val tokenType: String? = null,

    @SerialName("expires_in")
    val expiresIn: Int,

    @SerialName("refresh_token")
    val refreshToken: String? = null,
)