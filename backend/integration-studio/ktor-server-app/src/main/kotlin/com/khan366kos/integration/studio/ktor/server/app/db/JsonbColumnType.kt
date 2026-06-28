package com.khan366kos.integration.studio.ktor.server.app.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGobject

class JsonbColumnType : ColumnType<String>() {
    override fun sqlType(): String = "JSONB"

    override fun valueFromDB(value: Any): String = when (value) {
        is PGobject -> value.value ?: ""
        is String -> value
        else -> error("Unexpected JSONB value type: ${value::class}")
    }

    override fun notNullValueToDB(value: String): Any = PGobject().also {
        it.type = "jsonb"
        it.value = value
    }
}

fun Table.jsonb(name: String): Column<String> = registerColumn(name, JsonbColumnType())
