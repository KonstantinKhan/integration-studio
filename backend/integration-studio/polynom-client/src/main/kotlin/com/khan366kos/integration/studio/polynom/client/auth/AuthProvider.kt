package com.khan366kos.integration.studio.polynom.client.auth

import com.khan366kos.integration.studio.polynom.client.AuthContext
import com.khan366kos.integration.studio.polynom.client.SessionId

interface AuthProvider {

    suspend fun getAuthContext(sessionId: SessionId): AuthContext

    suspend fun refreshAuth(sessionId: SessionId): AuthContext
}
