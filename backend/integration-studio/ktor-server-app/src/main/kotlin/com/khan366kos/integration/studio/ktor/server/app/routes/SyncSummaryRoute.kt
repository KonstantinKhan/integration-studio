package com.khan366kos.integration.studio.ktor.server.app.routes

import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRepository
import com.khan366kos.integration.studio.ktor.server.app.db.MigrationRunRow
import com.khan366kos.integration.studio.ktor.server.app.db.RunType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter

@Serializable
data class SyncRunInfo(
    val runId: String,
    val startedAt: String,
    val fromDate: String?,
    val toDate: String?,
    val processedCount: Int?,
)

@Serializable
data class SyncSummaryResponse(
    val lastAutoSync: SyncRunInfo?,
    val lastManualSync: SyncRunInfo?,
    val errorsBetween: Int,
)

private val ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun Route.syncSummary(repository: MigrationRepository): Route = route("sync/summary") {
    get {
        val lastAuto   = repository.getLastSuccessfulRun(RunType.AUTO)
        val lastManual = repository.getLastSuccessfulRun(RunType.MANUAL)

        val errorsBetween = if (lastAuto != null && lastManual != null) {
            repository.countFailedRunsBetween(lastAuto.startedAt, lastManual.startedAt)
        } else 0

        call.respond(
            HttpStatusCode.OK,
            SyncSummaryResponse(
                lastAutoSync   = lastAuto?.toInfo(),
                lastManualSync = lastManual?.toInfo(),
                errorsBetween  = errorsBetween,
            )
        )
    }
}

private fun MigrationRunRow.toInfo() = SyncRunInfo(
    runId          = id.toString(),
    startedAt      = ISO.format(startedAt),
    fromDate       = fromDate?.let { ISO.format(it) },
    toDate         = toDate?.let { ISO.format(it) },
    processedCount = totalCount,
)
