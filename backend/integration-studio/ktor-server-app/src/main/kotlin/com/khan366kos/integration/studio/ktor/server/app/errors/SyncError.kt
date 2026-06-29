package com.khan366kos.integration.studio.ktor.server.app.errors

sealed class SyncError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ExternalApiConnectionError(cause: Throwable) : SyncError("Ошибка подключения к внешнему API", cause)
    class ExternalApiDataError(cause: Throwable) : SyncError("Ошибка при получении данных от внешнего API", cause)
    class BrokerConnectionError(cause: Throwable) : SyncError("Ошибка подключения к брокеру", cause)
    class DatabaseError(cause: Throwable) : SyncError("Ошибка подключения к базе данных", cause)
    class ServiceError(cause: Throwable) : SyncError("Внутренняя ошибка сервиса", cause)
}

fun Throwable.toSyncError(): SyncError = when (this) {
    is SyncError -> this
    is java.sql.SQLException -> SyncError.DatabaseError(this)
    is org.jetbrains.exposed.exceptions.ExposedSQLException -> SyncError.DatabaseError(this)
    is com.rabbitmq.client.ShutdownSignalException -> SyncError.BrokerConnectionError(this)
    is io.ktor.client.plugins.ResponseException -> SyncError.ExternalApiDataError(this)
    is io.ktor.client.network.sockets.ConnectTimeoutException -> SyncError.ExternalApiConnectionError(this)
    is io.ktor.client.plugins.HttpRequestTimeoutException -> SyncError.ExternalApiConnectionError(this)
    is java.net.ConnectException -> SyncError.ExternalApiConnectionError(this)
    else -> this.cause?.toSyncError() ?: SyncError.ServiceError(this)
}
