package com.khan366kos.integration.studio.ktor.server.app.config

import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailConfig
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqConfig
import com.khan366kos.integration.studio.ktor.server.app.scheduling.SyncSchedulerConfig
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseSettings(
    val url: String,
    val user: String,
    val password: String,
    val poolSize: Int = 10,
)

@Serializable
data class PolynomSettings(
    val baseUrl: String,
)

@Serializable
data class LoodsmanSettings(
    val baseUrl: String,
)

@Serializable
data class AppSettings(
    val database: DatabaseSettings,
    val rabbitmq: RabbitMqConfig,
    val email: EmailConfig,
    val scheduler: SyncSchedulerConfig,
    val polynom: PolynomSettings,
    val loodsman: LoodsmanSettings,
)
