package com.khan366kos.integration.studio.ktor.server.app

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val id: String,
    val username: String,
)
