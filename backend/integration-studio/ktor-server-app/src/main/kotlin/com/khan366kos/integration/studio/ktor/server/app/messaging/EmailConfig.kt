package com.khan366kos.integration.studio.ktor.server.app.messaging

import kotlinx.serialization.Serializable

@Serializable
data class EmailConfig(
    val enabled: Boolean,
    val smtpHost: String,
    val smtpPort: Int,
    val smtpTls: Boolean,
    val from: String,
    val password: String,
    val to: String,
)
