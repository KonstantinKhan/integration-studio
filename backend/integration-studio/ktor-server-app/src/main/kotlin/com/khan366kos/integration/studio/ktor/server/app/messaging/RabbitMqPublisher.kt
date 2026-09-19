package com.khan366kos.integration.studio.ktor.server.app.messaging

import com.khan366kos.domain.polynom.PolynomElement
import com.khan366kos.integration.studio.ktor.server.app.connection.RabbitManager
import com.khan366kos.integration.studio.ktor.server.app.errors.ServiceUnavailableException
import com.khan366kos.integration.studio.ktor.server.app.messaging.model.MigrationEventMessage
import com.rabbitmq.client.AMQP
import kotlinx.serialization.json.Json

/**
 * Publishes migration events. The connection is owned by [RabbitManager] and
 * may not be established yet — in that case publishing fails fast with
 * [ServiceUnavailableException] (mapped to HTTP 503).
 */
class RabbitMqPublisher(
    private val rabbitManager: RabbitManager,
    private val json: Json,
) {

    fun publish(element: PolynomElement, runId: java.util.UUID) {
        val settings = rabbitManager.currentSettings
            ?: throw ServiceUnavailableException("rabbitmq")
        val connection = rabbitManager.current?.takeIf { it.isOpen }
            ?: throw ServiceUnavailableException("rabbitmq")

        val message = MigrationEventMessage(
            migrationRunId = runId.toString(),
            name           = element.designation,
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

        try {
            connection.createChannel().use { channel ->
                channel.confirmSelect()
                channel.basicPublish(settings.exchange, settings.routingKey, props, body)
                channel.waitForConfirmsOrDie(5_000)
            }
        } catch (e: Exception) {
            throw ServiceUnavailableException("rabbitmq", "Publish failed: ${e.message}")
        }
    }
}
