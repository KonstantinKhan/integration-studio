package com.khan366kos.integration.studio.ktor.server.app.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(url: String, user: String, password: String, poolSize: Int = 10) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl         = url
            username        = user
            this.password   = password
            maximumPoolSize = poolSize
            driverClassName = "org.postgresql.Driver"
            isAutoCommit    = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        Database.connect(HikariDataSource(hikariConfig))
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                MigrationRunsTable,
                MigrationEventsTable
            )
        }
    }
}
