package com.khan366kos.integration.studio.ktor.server.app.scheduling

data class SyncSchedulerConfig(
    val enabled: Boolean,
    val intervalMinutes: Long,
    val scopeTypeId: Int,
    val scopeObjectId: Int,
    val serviceUser: String,
    val servicePassword: String,
    val serviceStorageId: String,
    val externalApiTimezoneOffsetMinutes: Int = 0,
)
