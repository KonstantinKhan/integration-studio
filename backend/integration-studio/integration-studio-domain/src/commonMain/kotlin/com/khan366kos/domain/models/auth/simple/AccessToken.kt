package com.khan366kos.domain.models.auth.simple

@JvmInline
value class AccessToken(val value: String) {
    fun asString() = value

    companion object {
        val NONE = AccessToken("")
    }
}