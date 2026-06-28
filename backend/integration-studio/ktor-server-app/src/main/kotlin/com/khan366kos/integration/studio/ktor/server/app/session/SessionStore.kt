package com.khan366kos.integration.studio.ktor.server.app.session

import com.khan366kos.domain.models.auth.UserCredentials

interface SessionStore {
    fun store(sessionId: String, credentials: UserCredentials)
    fun retrieve(sessionId: String): UserCredentials?
    fun updateCredentials(sessionId: String, newCredentials: UserCredentials)
    fun remove(sessionId: String)
    fun cleanup(expirationThresholdMs: Long)
}