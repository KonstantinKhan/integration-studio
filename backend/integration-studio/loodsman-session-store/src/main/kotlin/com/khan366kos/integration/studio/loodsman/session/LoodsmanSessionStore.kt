package com.khan366kos.integration.studio.loodsman.session

interface LoodsmanSessionStore {
    fun store(sessionId: String, session: LoodsmanSession)
    fun retrieve(sessionId: String): LoodsmanSession?
    fun remove(sessionId: String)
    fun cleanup(expirationThresholdMs: Long)
}
