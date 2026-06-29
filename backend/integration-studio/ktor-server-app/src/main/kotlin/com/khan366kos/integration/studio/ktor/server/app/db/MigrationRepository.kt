package com.khan366kos.integration.studio.ktor.server.app.db

import com.khan366kos.domain.polynom.PolynomElement
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.OffsetDateTime
import java.util.UUID

data class MigrationRunRow(
    val id: UUID,
    val startedAt: OffsetDateTime,
    val status: String,
    val totalCount: Int?,
    val type: String?,
    val fromDate: OffsetDateTime?,
    val toDate: OffsetDateTime?,
)

class MigrationRepository(private val json: Json) {

    suspend fun createRun(
        id: UUID,
        startedAt: OffsetDateTime,
        type: String,
        fromDate: OffsetDateTime?,
        toDate: OffsetDateTime?,
    ) {
        newSuspendedTransaction {
            MigrationRunsTable.insert { row ->
                row[MigrationRunsTable.id]        = id
                row[MigrationRunsTable.startedAt] = startedAt
                row[MigrationRunsTable.status]    = RunStatus.RUNNING
                row[MigrationRunsTable.createdAt] = OffsetDateTime.now()
                row[MigrationRunsTable.type]      = type
                row[MigrationRunsTable.fromDate]  = fromDate
                row[MigrationRunsTable.toDate]    = toDate
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

    suspend fun updateRunSendingStarted(id: UUID, at: OffsetDateTime) {
        newSuspendedTransaction {
            MigrationRunsTable.update({ MigrationRunsTable.id eq id }) { row ->
                row[MigrationRunsTable.sendingStartedAt] = at
            }
        }
    }

    suspend fun updateRunErrorType(id: UUID, errorType: String) {
        newSuspendedTransaction {
            MigrationRunsTable.update({ MigrationRunsTable.id eq id }) { row ->
                row[MigrationRunsTable.errorType] = errorType
            }
        }
    }

    suspend fun getLastSuccessfulRun(type: String): MigrationRunRow? =
        newSuspendedTransaction {
            MigrationRunsTable
                .selectAll()
                .where {
                    (MigrationRunsTable.status eq RunStatus.COMPLETED) and
                    (MigrationRunsTable.type eq type)
                }
                .orderBy(MigrationRunsTable.startedAt, SortOrder.DESC)
                .limit(1)
                .map { it.toMigrationRunRow() }
                .firstOrNull()
        }

    suspend fun countFailedRunsBetween(from: OffsetDateTime, to: OffsetDateTime): Int =
        newSuspendedTransaction {
            val minTs = if (from.isBefore(to)) from else to
            val maxTs = if (from.isBefore(to)) to else from
            MigrationRunsTable
                .selectAll()
                .where {
                    (MigrationRunsTable.status eq RunStatus.FAILED) and
                    (MigrationRunsTable.startedAt greaterEq minTs) and
                    (MigrationRunsTable.startedAt lessEq maxTs)
                }
                .count()
                .toInt()
        }

    private fun org.jetbrains.exposed.sql.ResultRow.toMigrationRunRow() = MigrationRunRow(
        id         = this[MigrationRunsTable.id],
        startedAt  = this[MigrationRunsTable.startedAt],
        status     = this[MigrationRunsTable.status],
        totalCount = this[MigrationRunsTable.totalCount],
        type       = this[MigrationRunsTable.type],
        fromDate   = this[MigrationRunsTable.fromDate],
        toDate     = this[MigrationRunsTable.toDate],
    )
}
