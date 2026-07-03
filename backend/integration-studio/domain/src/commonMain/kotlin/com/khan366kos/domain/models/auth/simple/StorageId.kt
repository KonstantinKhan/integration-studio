package com.khan366kos.domain.models.auth.simple

@JvmInline
value class StorageId(val value: String) {
    fun asString() = value

    companion object {
        val NONE = StorageId("")
    }
}