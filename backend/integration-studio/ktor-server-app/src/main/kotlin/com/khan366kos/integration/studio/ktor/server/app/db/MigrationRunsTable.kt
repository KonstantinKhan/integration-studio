package com.khan366kos.integration.studio.ktor.server.app.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZoneWithTimeZone

object MigrationRunsTable : Table("migration_runs") {
    val id         = uuid("id")
    val startedAt  = timestampWithTimeZone("started_at")
    val status     = varchar("status", 20)
    val totalCount = integer("total_count").nullable()
    val createdAt  = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}

object RunStatus {
    const val RUNNING   = "running"
    const val COMPLETED = "completed"
    const val FAILED    = "failed"
}
