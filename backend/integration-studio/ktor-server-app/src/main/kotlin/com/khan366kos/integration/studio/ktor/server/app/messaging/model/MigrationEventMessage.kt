package com.khan366kos.integration.studio.ktor.server.app.messaging.model

import com.khan366kos.domain.polynom.PropertyResult
import kotlinx.serialization.Serializable

@Serializable
data class MigrationEventMessage(
    val migrationRunId: String,
    val name: String,
    val typeId: Int,
    val objectId: Int,
    val properties: List<PropertyResult>,
)
