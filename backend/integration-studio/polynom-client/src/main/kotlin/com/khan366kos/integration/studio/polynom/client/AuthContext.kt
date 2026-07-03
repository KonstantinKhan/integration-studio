package com.khan366kos.integration.studio.polynom.client

import com.khan366kos.domain.models.auth.UserCredentials

data class AuthContext(
    val sessionId: SessionId,
    val credentials: UserCredentials
) {
    companion object {
        val NONE = AuthContext(
            sessionId = SessionId.NONE,
            credentials = UserCredentials()
        )
    }
}