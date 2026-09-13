package com.khan366kos.integration.studio.transport.loodsman.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginInputDto(
    val dbName: String? = null,
    val username: String? = null,
    val password: String? = null,
    val rememberMe: Boolean? = null,
)
