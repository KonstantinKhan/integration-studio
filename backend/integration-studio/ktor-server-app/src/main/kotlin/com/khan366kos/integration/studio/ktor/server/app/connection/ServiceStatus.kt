package com.khan366kos.integration.studio.ktor.server.app.connection

import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicReference

enum class ServiceState {
    CONNECTING,
    CONNECTED,
    FAILED,
}

@Serializable
data class ServiceStatus(
    val state: String,
    val lastError: String? = null,
    val latencyMs: Long? = null,
)

class StatusHolder(initial: ServiceState = ServiceState.CONNECTING) {
    private val ref = AtomicReference(ServiceStatus(initial.name))

    var status: ServiceStatus
        get() = ref.get()
        private set(value) {
            ref.set(value)
        }

    fun connecting() {
        ref.set(ServiceStatus(ServiceState.CONNECTING.name))
    }

    fun connected(latencyMs: Long?) {
        ref.set(ServiceStatus(ServiceState.CONNECTED.name, latencyMs = latencyMs))
    }

    fun failed(error: String?) {
        ref.set(ServiceStatus(ServiceState.FAILED.name, lastError = error))
    }

    fun disconnected(error: String?) {
        ref.set(ServiceStatus(ServiceState.FAILED.name, lastError = error))
    }
}
