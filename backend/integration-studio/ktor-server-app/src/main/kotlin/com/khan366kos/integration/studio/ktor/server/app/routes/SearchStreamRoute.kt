package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.ktor.server.app.config.AppConfig
import com.khan366kos.integration.studio.ktor.server.app.plugins.userSession
import com.khan366kos.integration.studio.ktor.server.app.streaming.MigrationStatus
import com.khan366kos.integration.studio.ktor.server.app.streaming.MigrationStreamRegistry
import com.khan366kos.integration.studio.ktor.server.app.streaming.SseEvent
import com.khan366kos.integration.studio.ktor.server.app.streaming.eventName
import com.khan366kos.integration.studio.transport.polynom.request.IPropertySearchRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sse.sse
import kotlinx.serialization.json.Json

/**
 * SSE-стриминг миграции изменений.
 *
 * 1 активный поток на пользователя (по sessionId).
 * - POST /search/streams/start            -> старт, {streamId} или 409 + {streamId} если уже RUNNING
 * - GET  /search/streams/{id}/status      -> snapshot статуса (для recovery после refresh)
 * - GET  /search/streams/{id}/events      -> SSE tail-событий (только свежие после подписки)
 */
fun Route.searchStream(config: AppConfig): Route = route("search/streams") {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        classDiscriminator = "type"
    }
    val registry: MigrationStreamRegistry = config.migrationStreamRegistry

    post("start") {
        val request = call.receive<IPropertySearchRequest>()
        val sessionId = call.userSession.id

        // Если уже есть RUNNING — вернуть его streamId (reconnect-сценарий).
        registry.runningFor(sessionId)?.let { active ->
            call.respond(
                HttpStatusCode.Conflict,
                mapOf(
                    "streamId" to active.streamId,
                    "status" to "RUNNING",
                    "message" to "Stream already running"
                )
            )
            return@post
        }

        val stream = try {
            registry.start(sessionId, config.polynomApplicationService, request)
        } catch (e: IllegalStateException) {
            // Race: стартанул параллельный запрос.
            val active = registry.runningFor(sessionId)
            call.respond(
                HttpStatusCode.Conflict,
                mapOf(
                    "streamId" to (active?.streamId ?: ""),
                    "status" to "RUNNING",
                    "message" to (e.message ?: "Stream already running")
                )
            )
            return@post
        }

        call.respond(HttpStatusCode.OK, mapOf("streamId" to stream.streamId))
    }

    get("{streamId}/status") {
        val streamId = call.parameters["streamId"]
        if (streamId == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "streamId is required"))
            return@get
        }
        val snapshot = registry.statusOf(streamId)
        if (snapshot == null) {
            call.respond(
                HttpStatusCode.NotFound,
                mapOf("error" to "Stream not found or already finished")
            )
            return@get
        }
        call.respond(HttpStatusCode.OK, snapshot)
    }

    sse("{streamId}/events") {
        val streamId = call.parameters["streamId"]
        if (streamId == null) {
            send(
                data = json.encodeToString(SseEvent.serializer(), SseEvent.Error(message = "streamId is required")),
                event = "error"
            )
            close()
            return@sse
        }

        val stream = registry.streamOf(streamId)
        if (stream == null) {
            send(
                data = json.encodeToString(SseEvent.serializer(), SseEvent.Error(message = "Stream not found")),
                event = "error"
            )
            close()
            return@sse
        }

        // Если уже терминальный — отдаём финальное событие и закрываемся.
        if (stream.status != MigrationStatus.RUNNING) {
            val terminalEvent: SseEvent = when (stream.status) {
                MigrationStatus.COMPLETED -> SseEvent.Done(processed = stream.processedCount)
                MigrationStatus.FAILED -> SseEvent.Error(message = stream.errorMessage ?: "Failed")
                MigrationStatus.RUNNING -> error("unreachable")
            }
            send(
                data = json.encodeToString(SseEvent.serializer(), terminalEvent),
                event = terminalEvent.eventName()
            )
            close()
            registry.maybeGc(streamId)
            return@sse
        }

        stream.acquireSubscriber()
        try {
            stream.events.collect { event ->
                send(
                    data = json.encodeToString(SseEvent.serializer(), event),
                    event = event.eventName()
                )
            }
        } catch (e: Throwable) {
            send(
                data = json.encodeToString(
                    SseEvent.serializer(),
                    SseEvent.Error(message = e.message ?: "Connection error")
                ),
                event = "error"
            )
        } finally {
            stream.releaseSubscriber()
            registry.maybeGc(streamId)
        }
    }
}
