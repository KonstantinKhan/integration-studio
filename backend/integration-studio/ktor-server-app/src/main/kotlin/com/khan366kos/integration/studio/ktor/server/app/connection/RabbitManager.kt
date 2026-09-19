package com.khan366kos.integration.studio.ktor.server.app.connection

import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqConfig
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.ShutdownListener
import com.rabbitmq.client.ShutdownSignalException
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

/**
 * Owns the RabbitMQ connection. Connects in the background (never blocks
 * startup). The Java client's automatic recovery handles reconnects after
 * runtime connection drops; [ReconnectLoop] handles the initial connect and
 * settings changes.
 */
class RabbitManager {

    private val log = LoggerFactory.getLogger(RabbitManager::class.java)

    private val connectionRef = AtomicReference<Connection?>(null)
    private val settingsRef = AtomicReference<RabbitMqConfig?>(null)
    val status = StatusHolder()

    val current: Connection?
        get() = connectionRef.get()

    val currentSettings: RabbitMqConfig?
        get() = settingsRef.get()

    /**
     * Connects using the given settings. Throws if the broker is unreachable,
     * so it must be called from a background reconnect loop.
     */
    fun connect(settings: RabbitMqConfig) {
        status.connecting()
        val startedAt = System.currentTimeMillis()
        val factory = ConnectionFactory().apply {
            host                       = settings.host
            port                       = settings.port
            virtualHost                = settings.vhost
            username                   = settings.user
            password                   = settings.password
            isAutomaticRecoveryEnabled = true
            isTopologyRecoveryEnabled  = true
            networkRecoveryInterval    = 5_000
            setConnectionTimeout(5_000)
        }
        val connection = factory.newConnection()
        connection.addShutdownListener(ShutdownListener { cause ->
            if (cause is ShutdownSignalException && !cause.isInitiatedByApplication) {
                status.disconnected(cause.message)
                log.warn("RabbitMQ connection lost: {}", cause.message)
            }
        })
        connection.createChannel().use { channel ->
            // Idempotent — safe if the exchange already exists with same params.
            channel.exchangeDeclare(settings.exchange, "topic", true)
        }
        val previous = connectionRef.getAndSet(connection)
        settingsRef.set(settings)
        previous?.close()
        status.connected(System.currentTimeMillis() - startedAt)
        log.info("RabbitMQ connected to {}:{}/{} in {} ms",
            settings.host, settings.port, settings.vhost, System.currentTimeMillis() - startedAt)
    }

    fun close() {
        connectionRef.getAndSet(null)?.close()
        settingsRef.set(null)
    }
}
