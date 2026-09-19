package com.khan366kos.integration.studio.ktor.server.app.config

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.concurrent.atomic.AtomicReference

/**
 * Settings overridden via UI. Stored in a local JSON file so that they survive
 * restarts. Seed values come from application.conf / environment variables.
 */
class AppSettingsStore(
    filePath: Path,
    seed: AppSettings,
) {
    @Serializable
    data class AppSettingsUpdate(
        val database: DatabaseSettings? = null,
        val rabbitmq: RabbitSecretsUpdate? = null,
        val email: EmailSecretsUpdate? = null,
        val scheduler: SchedulerSecretsUpdate? = null,
        val polynom: PolynomSettings? = null,
        val loodsman: LoodsmanSettings? = null,
    )

    /**
     * Partial updates for sections that contain secret fields. A null (or blank,
     * for strings) secret field keeps the previously stored value.
     */
    @Serializable
    data class RabbitSecretsUpdate(
        val host: String? = null,
        val port: Int? = null,
        val vhost: String? = null,
        val user: String? = null,
        val password: String? = null,
        val exchange: String? = null,
        val routingKey: String? = null,
    )

    @Serializable
    data class EmailSecretsUpdate(
        val enabled: Boolean? = null,
        val smtpHost: String? = null,
        val smtpPort: Int? = null,
        val smtpTls: Boolean? = null,
        val from: String? = null,
        val password: String? = null,
        val to: String? = null,
    )

    @Serializable
    data class SchedulerSecretsUpdate(
        val enabled: Boolean? = null,
        val intervalMinutes: Long? = null,
        val scopeTypeId: Int? = null,
        val scopeObjectId: Int? = null,
        val serviceUser: String? = null,
        val servicePassword: String? = null,
        val serviceStorageId: String? = null,
        val externalApiTimezoneOffsetMinutes: Int? = null,
    )

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val writeMutex = Mutex()
    private val current = AtomicReference(seed)

    val filePath: Path = filePath

    val currentSettings: AppSettings
        get() = current.get()

    fun loadFromDisk() {
        if (!Files.exists(filePath)) return
        val text = Files.readString(filePath)
        val stored = json.decodeFromString<AppSettings>(text)
        current.set(stored)
    }

    suspend fun update(transform: (AppSettings) -> AppSettings): AppSettings =
        writeMutex.withLock {
            val next = transform(current.get())
            Files.createDirectories(filePath.toAbsolutePath().parent)
            val tmp = filePath.resolveSibling(filePath.fileName.toString() + ".tmp")
            Files.writeString(tmp, json.encodeToString(AppSettings.serializer(), next))
            Files.move(tmp, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            current.set(next)
            next
        }

    suspend fun applyUpdate(update: AppSettingsUpdate): AppSettings = update { old ->
        val db = update.database
        val rabbit = update.rabbitmq
        val email = update.email
        val scheduler = update.scheduler

        old.copy(
            database = db ?: old.database,
            rabbitmq = rabbit?.let {
                old.rabbitmq.copy(
                    host = it.host?.takeIf(String::isNotBlank) ?: old.rabbitmq.host,
                    port = it.port ?: old.rabbitmq.port,
                    vhost = it.vhost?.takeIf(String::isNotBlank) ?: old.rabbitmq.vhost,
                    user = it.user?.takeIf(String::isNotBlank) ?: old.rabbitmq.user,
                    password = it.password?.takeIf(String::isNotBlank) ?: old.rabbitmq.password,
                    exchange = it.exchange?.takeIf(String::isNotBlank) ?: old.rabbitmq.exchange,
                    routingKey = it.routingKey?.takeIf(String::isNotBlank) ?: old.rabbitmq.routingKey,
                )
            } ?: old.rabbitmq,
            email = email?.let {
                old.email.copy(
                    enabled = it.enabled ?: old.email.enabled,
                    smtpHost = it.smtpHost?.takeIf(String::isNotBlank) ?: old.email.smtpHost,
                    smtpPort = it.smtpPort ?: old.email.smtpPort,
                    smtpTls = it.smtpTls ?: old.email.smtpTls,
                    from = it.from?.takeIf(String::isNotBlank) ?: old.email.from,
                    password = it.password?.takeIf(String::isNotBlank) ?: old.email.password,
                    to = it.to?.takeIf(String::isNotBlank) ?: old.email.to,
                )
            } ?: old.email,
            scheduler = scheduler?.let {
                old.scheduler.copy(
                    enabled = it.enabled ?: old.scheduler.enabled,
                    intervalMinutes = it.intervalMinutes ?: old.scheduler.intervalMinutes,
                    scopeTypeId = it.scopeTypeId ?: old.scheduler.scopeTypeId,
                    scopeObjectId = it.scopeObjectId ?: old.scheduler.scopeObjectId,
                    serviceUser = it.serviceUser?.takeIf(String::isNotBlank) ?: old.scheduler.serviceUser,
                    servicePassword = it.servicePassword?.takeIf(String::isNotBlank) ?: old.scheduler.servicePassword,
                    serviceStorageId = it.serviceStorageId?.takeIf(String::isNotBlank)
                        ?: old.scheduler.serviceStorageId,
                    externalApiTimezoneOffsetMinutes = it.externalApiTimezoneOffsetMinutes
                        ?: old.scheduler.externalApiTimezoneOffsetMinutes,
                )
            } ?: old.scheduler,
            polynom = update.polynom?.takeIf { it.baseUrl.isNotBlank() } ?: old.polynom,
            loodsman = update.loodsman?.takeIf { it.baseUrl.isNotBlank() } ?: old.loodsman,
        )
    }

    companion object {
        fun resolvePath(raw: String?): Path =
            Path.of(raw ?: "data/connection-settings.json")
    }
}
