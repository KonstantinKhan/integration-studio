package com.khan366kos.integration.studio.ktor.server.app.db

import com.khan366kos.domain.polynom.PolynomElement
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime
import java.util.UUID

class MigrationRepository(private val json: Json) {

    suspend fun createRun(id: UUID, startedAt: OffsetDateTime) {
        newSuspendedTransaction {
            MigrationRunsTable.insert { row ->
                row[MigrationRunsTable.id]        = id
                row[MigrationRunsTable.startedAt] = startedAt
                row[MigrationRunsTable.status]    = RunStatus.RUNNING
                row[MigrationRunsTable.createdAt] = OffsetDateTime.now()
            }
        }
    }

    suspend fun insertEvent(runId: UUID, element: PolynomElement): UUID {
        val eventId = UUID.randomUUID()
        val now = OffsetDateTime.now()
        newSuspendedTransaction {
            MigrationEventsTable.insert { row ->
                row[MigrationEventsTable.id]              = eventId
                row[MigrationEventsTable.runId]           = runId
                row[MigrationEventsTable.name]            = element.name
                row[MigrationEventsTable.typeId]          = element.typeId
                row[MigrationEventsTable.objectId]        = element.objectId
                row[MigrationEventsTable.payload]         = json.encodeToString(PolynomElement.serializer(), element)
                row[MigrationEventsTable.status]          = EventStatus.PREPARING
                row[MigrationEventsTable.statusUpdatedAt] = now
                row[MigrationEventsTable.createdAt]       = now
            }
        }
        return eventId
    }

    suspend fun updateEventStatus(id: UUID, newStatus: String) {
        newSuspendedTransaction {
            MigrationEventsTable.update({ MigrationEventsTable.id eq id }) { row ->
                row[MigrationEventsTable.status]          = newStatus
                row[MigrationEventsTable.statusUpdatedAt] = OffsetDateTime.now()
            }
        }
    }

    suspend fun updateRunStatus(id: UUID, newStatus: String, total: Int) {
        newSuspendedTransaction {
            MigrationRunsTable.update({ MigrationRunsTable.id eq id }) { row ->
                row[MigrationRunsTable.status]     = newStatus
                row[MigrationRunsTable.totalCount] = total
            }
        }
    }
}
