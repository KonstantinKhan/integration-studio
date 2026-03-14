package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationRequestTransport(
    @SerialName("login")
    val username: String,

    @SerialName("password")
    val password: String,

    @SerialName("storageId")
    val storageId: String
)