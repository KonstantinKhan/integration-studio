package com.khan366kos.integration.studio.ktor.server.app.streaming

import com.khan366kos.integration.studio.bff.transport.request.PolynomElementFromPeriodRequestBffDto
import com.khan366kos.integration.studio.bff.transport.models.PolynomElementBffDto
import com.khan366kos.integration.studio.ktor.server.app.db.EventStatus
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.db.RunStatus
import com.khan366kos.integration.studio.ktor.server.app.db.RunType
import com.khan366kos.integration.studio.ktor.server.app.errors.SyncError
import com.khan366kos.integration.studio.ktor.server.app.errors.toSyncError
import com.khan366kos.integration.studio.ktor.server.app.messaging.EmailNotifier
import com.khan366kos.integration.studio.ktor.server.app.messaging.RabbitMqPublisher
import com.khan366kos.integration.studio.logics.PolynomApplicationService
import com.khan366kos.integration.studio.mapping.toBffDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

enum class MigrationStatus {
    @SerialName("RUNNING") RUNNING,
    @SerialName("COMPLETED") COMPLETED,
    @SerialName("FAILED") FAILED
}

@Serializable
sealed class SseEvent {

    @Serializable
    @SerialName("started")
    data class Started(
        @SerialName("streamId") val streamId: String
    ) : SseEvent()

    @Serializable
    @SerialName("item")
    data class Item(
        @SerialName("item") val item: PolynomElementBffDto
    ) : SseEvent()

    @Serializable
    @SerialName("progress")
    data class Progress(
        @SerialName("processed") val processed: Int
    ) : SseEvent()

    @Serializable
    @SerialName("error")
    data class Error(
        @SerialName("message") val message: String
    ) : SseEvent()

    @Serializable
    @SerialName("done")
    data class Done(
        @SerialName("processed") val processed: Int
    ) : SseEvent()
}

fun SseEvent.eventName(): String = when (this) {
    is SseEvent.Started -> "started"
    is SseEvent.Item -> "item"
    is SseEvent.Progress -> "progress"
    is SseEvent.Error -> "error"
    is SseEvent.Done -> "done"
}

@Serializable
data class StreamStatusResponse(
    @SerialName("streamId") val streamId: String,
    @SerialName("status") val status: MigrationStatus,
    @SerialName("processedCount") val processedCount: Int,
    @SerialName("errorMessage") val errorMessage: String? = null
)

class MigrationStream(
    val streamId: String,
    val sessionId: String,
    private val _events: MutableSharedFlow<SseEvent>
) {
    val events: SharedFlow<SseEvent> get() = _events.asSharedFlow()

    private val _status = AtomicReference(MigrationStatus.RUNNING)
    val status: MigrationStatus get() = _status.get()

    private val _processedCount = AtomicInteger(0)
    val processedCount: Int get() = _processedCount.get()

    private val _errorMessage = AtomicReference<String?>(null)
    val errorMessage: String? get() = _errorMessage.get()

    @Volatile
    var job: Job? = null

    private val _subscribers = AtomicInteger(0)
    val subscribers: Int get() = _subscribers.get()

    fun acquireSubscriber(): Boolean {
        _subscribers.incrementAndGet()
        return true
    }

    fun releaseSubscriber() {
        _subscribers.decrementAndGet()
    }

    internal fun markProcessed(): Int = _processedCount.incrementAndGet()
    internal fun snapshot(): StreamStatusResponse = StreamStatusResponse(
        streamId = streamId,
        status = _status.get(),
        processedCount = _processedCount.get(),
        errorMessage = _errorMessage.get()
    )

    internal suspend fun emit(event: SseEvent) {
        _events.emit(event)
    }

    internal fun completeSuccess() {
        _status.set(MigrationStatus.COMPLETED)
    }

    internal fun completeFailure(message: String) {
        _errorMessage.set(message)
        _status.set(MigrationStatus.FAILED)
    }
}

class SyncStreamRegistry(
    private val scope: CoroutineScope,
    private val repository: MigrationRepository,
    private val publisher: RabbitMqPublisher,
    private val emailNotifier: EmailNotifier,
    private val externalApiTimezoneOffsetMinutes: Int = 0,
) {
    private val log = LoggerFactory.getLogger(SyncStreamRegistry::class.java)
    private val activeBySession = ConcurrentHashMap<String, MigrationStream>()
    private val byId = ConcurrentHashMap<String, MigrationStream>()

    fun activeStreamIdFor(sessionId: String): String? = activeBySession[sessionId]?.streamId

    fun statusOf(streamId: String): StreamStatusResponse? = byId[streamId]?.snapshot()

    fun streamOf(streamId: String): MigrationStream? = byId[streamId]

    fun runningFor(sessionId: String): MigrationStream? =
        activeBySession[sessionId]?.takeIf { it.status == MigrationStatus.RUNNING }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun start(
        sessionId: String,
        service: PolynomApplicationService,
        request: PolynomElementFromPeriodRequestBffDto,
        type: String = RunType.MANUAL,
        initiatedBy: String? = null,
    ): MigrationStream {
        runningFor(sessionId)?.let {
            throw IllegalStateException("Stream already running: ${it.streamId}")
        }

        val streamId = UUID.randomUUID().toString()
        val sink = MutableSharedFlow<SseEvent>(
            replay = 0,
            extraBufferCapacity = 256,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val stream = MigrationStream(streamId, sessionId, sink)

        activeBySession[sessionId] = stream
        byId[streamId] = stream

        val fromDate = request.from.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)
        val toDate = request.to.toJavaLocalDateTime().atOffset(ZoneOffset.UTC)

        val apiOffset = ZoneOffset.ofTotalSeconds(externalApiTimezoneOffsetMinutes * 60)
        val apiRequest = request.copy(
            from = fromDate.withOffsetSameInstant(apiOffset).toLocalDateTime().toKotlinLocalDateTime(),
            to = toDate.withOffsetSameInstant(apiOffset).toLocalDateTime().toKotlinLocalDateTime(),
        )

        stream.job = scope.launch {
            val runId = UUID.randomUUID()
            val startedAt = OffsetDateTime.now()
            repository.createRun(runId, startedAt, type, fromDate, toDate, initiatedBy)

            val sendingStarted = AtomicBoolean(false)
            var currentEventId: UUID? = null

            try {
                stream.emit(SseEvent.Started(streamId = streamId))
                service.searchObjectsEnriched(sessionId, apiRequest)
                    .onEach { item ->
                        currentEventId = null

                        val eventId = try {
                            repository.insertEvent(runId, item)
                        } catch (e: Exception) {
                            throw SyncError.DatabaseError(e)
                        }
                        currentEventId = eventId

                        try {
                            repository.updateEventStatus(eventId, EventStatus.PENDING)
                        } catch (e: Exception) {
                            throw SyncError.DatabaseError(e)
                        }

                        if (!sendingStarted.getAndSet(true)) {
                            try {
                                repository.updateRunSendingStarted(runId, OffsetDateTime.now())
                            } catch (e: Exception) {
                                log.warn("Failed to set sending_started_at for run {}: {}", runId, e.message)
                            }
                        }

                        try {
                            publisher.publish(item, runId)
                        } catch (e: Exception) {
                            throw SyncError.BrokerConnectionError(e)
                        }

                        try {
                            repository.updateEventStatus(eventId, EventStatus.DELIVERED)
                        } catch (e: Exception) {
                            throw SyncError.DatabaseError(e)
                        }

                        currentEventId = null
                        stream.markProcessed()
                        stream.emit(SseEvent.Item(item = item.toBffDto()))
                        stream.emit(SseEvent.Progress(processed = stream.processedCount))
                    }
                    .collect()

                repository.updateRunStatus(runId, RunStatus.COMPLETED, stream.processedCount)
                stream.completeSuccess()
                stream.emit(SseEvent.Done(processed = stream.processedCount))
            } catch (e: Throwable) {
                val syncError = if (e is SyncError) e else e.toSyncError()
                log.error("Sync run {} failed [{}]: {}", runId, syncError::class.simpleName, syncError.message, syncError.cause)

                currentEventId?.let { eid ->
                    try { repository.updateEventStatus(eid, EventStatus.FAILED) } catch (_: Exception) {}
                }
                try {
                    repository.updateRunStatus(runId, RunStatus.FAILED, stream.processedCount)
                    repository.updateRunErrorType(runId, syncError::class.simpleName ?: "ServiceError")
                } catch (_: Exception) {}

                emailNotifier.sendSyncError(runId, type, syncError)

                stream.completeFailure(syncError.message ?: "Unknown error")
                stream.emit(SseEvent.Error(message = syncError.message ?: "Unknown error"))
            } finally {
                activeBySession.compute(sessionId) { _, current ->
                    if (current?.streamId == streamId) null else current
                }
                maybeGc(streamId)
            }
        }

        return stream
    }

    fun maybeGc(streamId: String) {
        val stream = byId[streamId] ?: return
        if (stream.status != MigrationStatus.RUNNING && stream.subscribers == 0) {
            byId.remove(streamId)
            activeBySession.values.removeIf { it.streamId == streamId }
        }
    }

    fun clearForSession(sessionId: String) {
        activeBySession.remove(sessionId)
        byId.values.removeIf { it.sessionId == sessionId }
    }
}
