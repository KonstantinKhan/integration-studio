package com.khan366kos.integration.studio.ktor.server.app.messaging

import com.khan366kos.domain.polynom.PolynomElement
import com.khan366kos.integration.studio.ktor.server.app.messaging.model.MigrationEventMessage
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.serialization.json.Json
import java.io.Closeable
import java.util.UUID

class RabbitMqPublisher(private val config: RabbitMqConfig, private val json: Json) : Closeable {

    private val connection: Connection

    init {
        val factory = ConnectionFactory().apply {
            host        = config.host
            port        = config.port
            virtualHost = config.vhost
            username    = config.user
            password    = config.password
        }
        connection = factory.newConnection()
        // Declare durable topic exchange once on startup.
        // Idempotent — safe to call even if exchange already exists with same params.
        connection.createChannel().use { ch ->
            ch.exchangeDeclare(config.exchange, "topic", /* durable */ true)
        }
    }

    fun publish(element: PolynomElement, runId: UUID) {
        val message = MigrationEventMessage(
            migrationRunId = runId.toString(),
            name           = element.name,
            typeId         = element.typeId,
            objectId       = element.objectId,
            properties     = element.properties,
        )
        val body = json.encodeToString(MigrationEventMessage.serializer(), message)
            .toByteArray(Charsets.UTF_8)

        val props = AMQP.BasicProperties.Builder()
            .contentType("application/json")
            .contentEncoding("UTF-8")
            .headers(mapOf("x-migration-run-id" to runId.toString()))
            .deliveryMode(2)
            .build()

        connection.createChannel().use { channel ->
            channel.confirmSelect()
            channel.basicPublish(config.exchange, config.routingKey, props, body)
            channel.waitForConfirmsOrDie(5_000)
        }
    }

    override fun close() {
        if (connection.isOpen) connection.close()
    }
}
