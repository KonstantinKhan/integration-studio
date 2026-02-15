package com.khan366kos.integration.studio.ktor.server.app.session

import com.khan366kos.common.models.auth.UserCredentials
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap

data class SessionEntry(
    val credentialsFlow: MutableStateFlow<UserCredentials>,
    val createdAt: Long = System.currentTimeMillis()
)

class InMemorySessionStore : SessionStore {
    private val sessions = ConcurrentHashMap<String, SessionEntry>()

    override fun store(sessionId: String, credentials: UserCredentials) {
        sessions[sessionId] = SessionEntry(MutableStateFlow(credentials))
    }

    override fun retrieve(sessionId: String): UserCredentials? {
        return sessions[sessionId]?.credentialsFlow?.value
    }

    override fun updateCredentials(sessionId: String, newCredentials: UserCredentials) {
        val sessionEntry = sessions[sessionId]
        if (sessionEntry != null) {
            sessionEntry.credentialsFlow.value = newCredentials
        }
    }

    override fun remove(sessionId: String) {
        sessions.remove(sessionId)
    }

    override fun cleanup(expirationThresholdMs: Long) {
        val now = System.currentTimeMillis()
        sessions.entries.removeIf { (_, entry) ->
            now - entry.createdAt > expirationThresholdMs
        }
    }
}