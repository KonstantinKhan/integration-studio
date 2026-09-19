package com.khan366kos.integration.studio.ktor.server.app.connection

import com.khan366kos.integration.studio.ktor.server.app.config.DatabaseSettings
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

/**
 * Owns the Hikari pool and the Exposed [Database]. Connection attempts are
 * performed in the background (see ReconnectLoop), so an unreachable database
 * never blocks application startup. Exposes the current database for
 * Exposed transactions; `null` means "not connected".
 */
class DatabaseManager {

    private val log = LoggerFactory.getLogger(DatabaseManager::class.java)

    private val dataSourceRef = AtomicReference<HikariDataSource?>(null)
    private val databaseRef = AtomicReference<Database?>(null)
    val status = StatusHolder()

    val current: Database?
        get() = databaseRef.get()

    /**
     * Connects using the given settings. Throws if the database is unreachable,
     * so it must be called from a background reconnect loop.
     */
    fun connect(settings: DatabaseSettings) {
        status.connecting()
        val startedAt = System.currentTimeMillis()
        val hikariConfig = HikariConfig().apply {
            jdbcUrl              = settings.url
            username             = settings.user
            password             = settings.password
            maximumPoolSize      = settings.poolSize
            driverClassName      = "org.postgresql.Driver"
            isAutoCommit         = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            connectionTimeout    = 5_000
            validate()
        }
        val dataSource = HikariDataSource(hikariConfig)
        try {
            migrate(dataSource)
            val previous = dataSourceRef.getAndSet(dataSource)
            databaseRef.set(Database.connect(dataSource))
            previous?.close()
            status.connected(System.currentTimeMillis() - startedAt)
            log.info("PostgreSQL connected and migrated in {} ms", System.currentTimeMillis() - startedAt)
        } catch (e: Exception) {
            dataSource.close()
            status.failed(e.message ?: e.javaClass.simpleName)
            throw e
        }
    }

    private fun migrate(dataSource: HikariDataSource) {
        Flyway
            .configure()
            .dataSource(dataSource)
            // Existing databases created by the old SchemaUtils DDL have no
            // flyway history: baseline them at 1 so V1 is skipped there,
            // while fresh databases run V1 normally.
            .baselineOnMigrate(true)
            .baselineVersion("1")
            .load()
            .migrate()
    }

    fun close() {
        databaseRef.set(null)
        dataSourceRef.getAndSet(null)?.close()
    }
}
