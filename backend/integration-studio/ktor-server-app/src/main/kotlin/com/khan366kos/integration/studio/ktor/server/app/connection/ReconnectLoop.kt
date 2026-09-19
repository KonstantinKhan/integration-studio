package com.khan366kos.integration.studio.ktor.server.app.connection

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

/**
 * Background connection loop: keeps retrying [attempt] with exponential
 * backoff until it succeeds, then idles until [requestReconnect] is called
 * (e.g. after connection settings were changed via UI). Never blocks
 * application startup.
 */
class ReconnectLoop(
    private val scope: CoroutineScope,
    private val name: String,
    private val status: StatusHolder,
    private val attempt: () -> Unit,
) {
    private val log = LoggerFactory.getLogger(ReconnectLoop::class.java)
    private val kick = Channel<Unit>(Channel.CONFLATED)

    fun requestReconnect() {
        kick.trySend(Unit)
    }

    fun start() {
        scope.launch {
            var backoffMs = INITIAL_BACKOFF_MS
            while (isActive) {
                try {
                    attempt()
                    backoffMs = INITIAL_BACKOFF_MS
                    kick.receive()
                    log.info("[{}] settings changed, reconnecting", name)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    status.failed(e.message ?: e.javaClass.simpleName)
                    log.warn("[{}] connect failed: {} — retry in {} ms", name, e.message, backoffMs)
                    delay(backoffMs)
                    backoffMs = (backoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
                }
            }
        }
    }

    private companion object {
        const val INITIAL_BACKOFF_MS = 2_000L
        const val MAX_BACKOFF_MS = 60_000L
    }
}
