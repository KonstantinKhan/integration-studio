package com.khan366kos.etl.polynom.bff.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val storageId: String,
    val login: String,
    val password: String,
    val moduleName: String = "",
    val clientType: Int = 0
)

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String
)