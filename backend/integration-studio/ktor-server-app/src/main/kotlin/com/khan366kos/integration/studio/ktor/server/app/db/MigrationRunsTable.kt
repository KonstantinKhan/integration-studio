package com.khan366kos.integration.studio.ktor.server.app.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object MigrationRunsTable : Table("migration_runs") {
    val id               = uuid("id")
    val startedAt        = timestampWithTimeZone("started_at")
    val status           = varchar("status", 20)
    val totalCount       = integer("total_count").nullable()
    val createdAt        = timestampWithTimeZone("created_at")
    val type             = varchar("type", 10).nullable()
    val sendingStartedAt = timestampWithTimeZone("sending_started_at").nullable()
    val fromDate         = timestampWithTimeZone("from_date").nullable()
    val toDate           = timestampWithTimeZone("to_date").nullable()
    val errorType        = varchar("error_type", 60).nullable()

    override val primaryKey = PrimaryKey(id)
}

object RunStatus {
    const val RUNNING   = "running"
    const val COMPLETED = "completed"
    const val FAILED    = "failed"
}

object RunType {
    const val MANUAL = "manual"
    const val AUTO   = "auto"
}
