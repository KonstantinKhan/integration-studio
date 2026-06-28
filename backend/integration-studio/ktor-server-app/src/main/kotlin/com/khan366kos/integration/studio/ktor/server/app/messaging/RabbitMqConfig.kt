package com.khan366kos.integration.studio.ktor.server.app.messaging

data class RabbitMqConfig(
    val host: String,
    val port: Int,
    val vhost: String,
    val user: String,
    val password: String,
    val exchange: String,
    val routingKey: String,
)
