package com.khan366kos.integration.studio.ktor.server.app.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object MigrationEventsTable : Table("migration_events") {
    val id              = uuid("id")
    val runId           = uuid("run_id").references(MigrationRunsTable.id)
    val name            = text("name")
    val typeId          = integer("type_id")
    val objectId        = integer("object_id")
    val payload         = jsonb("payload")
    val status          = varchar("status", 20)
    val statusUpdatedAt = timestampWithTimeZone("status_updated_at")
    val createdAt       = timestampWithTimeZone("created_at")

    override val primaryKey = PrimaryKey(id)
}

object EventStatus {
    const val PREPARING = "preparing"
    const val PENDING   = "pending"
    const val DELIVERED = "delivered"
    const val FAILED    = "failed"
}
