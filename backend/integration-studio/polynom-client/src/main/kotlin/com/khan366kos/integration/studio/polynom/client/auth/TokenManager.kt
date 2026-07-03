package com.khan366kos.integration.studio.polynom.client.auth

import com.khan366kos.domain.models.auth.UserCredentials
import com.khan366kos.domain.models.auth.simple.AccessToken
import com.khan366kos.domain.models.auth.simple.RefreshToken
import com.khan366kos.integration.studio.polynom.client.config.AuthConfig
import com.khan366kos.integration.studio.transport.polynom.models.LoginResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class TokenManager(
    private val tokenRefreshApi: TokenRefreshApi
) {
    private val sessionMutexes = ConcurrentHashMap<String, Mutex>()

    fun needsRefresh(userCredentials: UserCredentials): Boolean {
        if (userCredentials.isInvalidAccessToken()) return true
        if (userCredentials.expiresAt == 0L) return true

        val now = System.currentTimeMillis()
        val remainingMs = userCredentials.expiresAt - now

        if (remainingMs <= 0) return true

        val totalLifetimeMs = userCredentials.expiresAt - userCredentials.issuedAt
        if (totalLifetimeMs <= 0) return true

        val elapsedFraction = (now - userCredentials.issuedAt).toDouble() / totalLifetimeMs
        return elapsedFraction >= AuthConfig.REFRESH_THRESHOLD
    }

    suspend fun getValidCredentials(
        sessionId: String,
        userCredentials: UserCredentials
    ): UserCredentials {
        if (!needsRefresh(userCredentials)) {
            return userCredentials
        }

        return mutexFor(sessionId).withLock {
            if (!needsRefresh(userCredentials)) {
                userCredentials
            } else {
                performAuthentication(userCredentials)
            }
        }
    }

    suspend fun authenticate(
        sessionId: String,
        userCredentials: UserCredentials
    ): UserCredentials {
        return mutexFor(sessionId).withLock {
            performAuthentication(userCredentials)
        }
    }

    fun removeSession(sessionId: String) {
        sessionMutexes.remove(sessionId)
    }

    private fun mutexFor(sessionId: String): Mutex =
        sessionMutexes.computeIfAbsent(sessionId) { Mutex() }

    private suspend fun performAuthentication(
        userCredentials: UserCredentials
    ): UserCredentials {
        val response = tokenRefreshApi.refreshToken(
            accessToken = userCredentials.accessToken,
            refreshToken = userCredentials.refreshToken
        )

        val now = System.currentTimeMillis()
        val expiresAt = now + (response.expiresIn * 1000L)

        return UserCredentials(
            login = userCredentials.login,
            storageId = userCredentials.storageId,
            accessToken = AccessToken(response.accessToken ?: ""),
            refreshToken = RefreshToken(response.refreshToken ?: ""),
            issuedAt = now,
            expiresAt = expiresAt
        )
    }

    companion object {
        val NONE: TokenManager = TokenManager(TokenRefreshApi.NONE)
    }
}

fun interface TokenRefreshApi {
    suspend fun refreshToken(accessToken: AccessToken, refreshToken: RefreshToken): LoginResponse

    companion object {
        val NONE: TokenRefreshApi = TokenRefreshApi { _, _ ->
            throw IllegalStateException("TokenRefreshApi.NONE не должен использоваться в продакшене")
        }
    }
}
