package com.khan366kos.integration.studio.loodsman.session

import java.util.concurrent.ConcurrentHashMap

data class LoodsmanSessionEntry(
    val session: LoodsmanSession,
    val createdAt: Long = System.currentTimeMillis()
)

class InMemoryLoodsmanSessionStore : LoodsmanSessionStore {
    private val sessions = ConcurrentHashMap<String, LoodsmanSessionEntry>()

    override fun store(sessionId: String, session: LoodsmanSession) {
        sessions[sessionId] = LoodsmanSessionEntry(session)
    }

    override fun retrieve(sessionId: String): LoodsmanSession? {
        return sessions[sessionId]?.session
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
