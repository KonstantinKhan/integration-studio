package com.khan366kos.integration.studio.ktor.server.app.streaming

import com.khan366kos.common.models.PropertyResult
import com.khan366kos.integration.studio.application.polynom.PolynomApplicationService
import com.khan366kos.integration.studio.ktor.server.app.dto.EnrichedSearchResultItem
import com.khan366kos.integration.studio.transport.polynom.request.IPropertySearchRequest
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
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
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Состояние миграционного потока.
 */
enum class MigrationStatus {
    @SerialName("RUNNING") RUNNING,
    @SerialName("COMPLETED") COMPLETED,
    @SerialName("FAILED") FAILED
}

/**
 * Событие, стримящееся на UI через SSE.
 * `type`-дискриминатор (classDiscriminator="type") используется как SSE event name.
 */
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
        @SerialName("item") val item: EnrichedSearchResultItem
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

/** SSE event name для [SseEvent] (совпадает с @SerialName подкласса). */
fun SseEvent.eventName(): String = when (this) {
    is SseEvent.Started -> "started"
    is SseEvent.Item -> "item"
    is SseEvent.Progress -> "progress"
    is SseEvent.Error -> "error"
    is SseEvent.Done -> "done"
}

/**
 * Ответ эндпоинта status.
 */
@Serializable
data class StreamStatusResponse(
    @SerialName("streamId") val streamId: String,
    @SerialName("status") val status: MigrationStatus,
    @SerialName("processedCount") val processedCount: Int,
    @SerialName("errorMessage") val errorMessage: String? = null
)

/**
 * Один активный поток миграции для пользователя.
 */
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

/**
 * Реестр активных потоков миграции.
 * Один активный поток на пользователя (по sessionId).
 *
 * TTL: поток держится пока Job RUNNING независимо от подписчиков,
 * чтобы пережить кратковременный disconnect (refresh вкладки).
 * GC когда Job завершён И подписчиков 0.
 */
class MigrationStreamRegistry(
    private val scope: CoroutineScope
) {
    /** sessionId -> активный stream */
    private val activeBySession = ConcurrentHashMap<String, MigrationStream>()
    /** streamId -> stream (включая терминальные, до освобождения подписчиков) */
    private val byId = ConcurrentHashMap<String, MigrationStream>()

    fun activeStreamIdFor(sessionId: String): String? = activeBySession[sessionId]?.streamId

    fun statusOf(streamId: String): StreamStatusResponse? = byId[streamId]?.snapshot()

    fun streamOf(streamId: String): MigrationStream? = byId[streamId]

    /**
     * Возвращает активный RUNNING поток для session если есть, иначе null.
     */
    fun runningFor(sessionId: String): MigrationStream? =
        activeBySession[sessionId]?.takeIf { it.status == MigrationStatus.RUNNING }

    /**
     * Стартовать новый поток. Если уже есть RUNNING — выбросит IllegalStateException.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun start(
        sessionId: String,
        service: PolynomApplicationService,
        request: IPropertySearchRequest
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

        stream.job = scope.launch {
            try {
                stream.emit(SseEvent.Started(streamId = streamId))
                service.searchObjectsEnriched(sessionId, request)
                    .onEach { item ->
                        stream.markProcessed()
                        stream.emit(SseEvent.Item(item = item))
                        stream.emit(SseEvent.Progress(processed = stream.processedCount))
                    }
                    .collect()
                stream.completeSuccess()
                stream.emit(SseEvent.Done(processed = stream.processedCount))
            } catch (e: Throwable) {
                stream.completeFailure(e.message ?: "Unknown error")
                stream.emit(SseEvent.Error(message = e.message ?: "Unknown error"))
            } finally {
                // Снимаем активную привязку. Сам stream остаётся в byId пока есть подписчики.
                activeBySession.compute(sessionId) { _, current ->
                    if (current?.streamId == streamId) null else current
                }
                maybeGc(streamId)
            }
        }

        return stream
    }

    /**
     * Освободить поток если Job завершён и подписчиков 0.
     */
    fun maybeGc(streamId: String) {
        val stream = byId[streamId] ?: return
        if (stream.status != MigrationStatus.RUNNING && stream.subscribers == 0) {
            byId.remove(streamId)
            activeBySession.values.removeIf { it.streamId == streamId }
        }
    }

    /**
     * Полная очистка по session (logout).
     */
    fun clearForSession(sessionId: String) {
        activeBySession.remove(sessionId)
        byId.values.removeIf { it.sessionId == sessionId }
    }
}
