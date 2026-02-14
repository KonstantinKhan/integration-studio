package com.khan366kos.common.models.auth.simple

@JvmInline
value class RefreshToken(val value: String) {
    fun asString() = value

    companion object {
        val NONE = RefreshToken("")
    }
}