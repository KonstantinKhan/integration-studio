package com.khan366kos.etl.polynom.bff.auth

import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.etl.polynom.bff.config.AuthConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class TokenManager {
    private val sessionMutexes = ConcurrentHashMap<String, Mutex>()

    private fun mutexFor(sessionId: String): Mutex =
        sessionMutexes.computeIfAbsent(sessionId) { Mutex() }

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
        userCredentials: UserCredentials,
        httpClient: HttpClient,
        baseUrl: String
    ): UserCredentials {
        if (!needsRefresh(userCredentials)) {
            return userCredentials
        }

        return mutexFor(sessionId).withLock {
            if (!needsRefresh(userCredentials)) {
                userCredentials
            } else {
                performAuthentication(userCredentials, httpClient, baseUrl)
            }
        }
    }

    suspend fun authenticate(
        sessionId: String,
        userCredentials: UserCredentials,
        httpClient: HttpClient,
        baseUrl: String
    ): UserCredentials {
        return mutexFor(sessionId).withLock {
            performAuthentication(userCredentials, httpClient, baseUrl)
        }
    }

    fun removeSession(sessionId: String) {
        sessionMutexes.remove(sessionId)
    }

    private suspend fun performAuthentication(
        userCredentials: UserCredentials,
        httpClient: HttpClient,
        baseUrl: String
    ): UserCredentials {
        val httpResponse = httpClient.patch("$baseUrl${AuthConfig.LOGIN_ENDPOINT}") {
            header(HttpHeaders.Authorization, "Bearer ${userCredentials.accessToken.value}")
            contentType(ContentType.Text.Plain)
            setBody(userCredentials.refreshToken.value)
        }

        val response: LoginResponse = httpResponse.body()

        val now = System.currentTimeMillis()
        val expiresAt = now + (response.expiresIn * 1000L)

        return UserCredentials(
            login = userCredentials.login,
            storageId = userCredentials.storageId,
            accessToken = AccessToken(response.accessToken),
            refreshToken = RefreshToken(response.refreshToken),
            issuedAt = now,
            expiresAt = expiresAt
        )
    }
}