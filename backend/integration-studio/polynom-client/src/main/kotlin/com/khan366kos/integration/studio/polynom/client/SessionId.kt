package com.khan366kos.integration.studio.polynom.client

@JvmInline
value class SessionId(val value: String) {

    fun asString(): String = value

    companion object {
        val NONE = SessionId("")
    }
}