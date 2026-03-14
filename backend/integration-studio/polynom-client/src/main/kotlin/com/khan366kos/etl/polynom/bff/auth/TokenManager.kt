package com.khan366kos.etl.polynom.bff.auth

import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.etl.polynom.bff.config.AuthConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Менеджер токенов для управления жизненным циклом учётных данных.
 * Отвечает за:
 * - Проверку необходимости обновления токена
 * - Синхронизацию обновления токенов для одной сессии
 * - Вызов API обновления через TokenRefreshApi
 *
 * Не зависит от Ktor напрямую, использует абстракцию TokenRefreshApi.
 */
class TokenManager(
    private val tokenRefreshApi: TokenRefreshApi
) {
    private val sessionMutexes = ConcurrentHashMap<String, Mutex>()

    /**
     * Проверяет, требуется ли обновление токена.
     *
     * @param userCredentials текущие учётные данные
     * @return true если токен требует обновления, false иначе
     */
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

    /**
     * Получает валидные учётные данные, обновляя их при необходимости.
     *
     * @param sessionId идентификатор сессии
     * @param userCredentials текущие учётные данные
     * @return актуальные учётные данные (возможно обновлённые)
     */
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

    /**
     * Принудительно обновляет учётные данные сессии.
     *
     * @param sessionId идентификатор сессии
     * @param userCredentials текущие учётные данные
     * @return обновлённые учётные данные
     */
    suspend fun authenticate(
        sessionId: String,
        userCredentials: UserCredentials
    ): UserCredentials {
        return mutexFor(sessionId).withLock {
            performAuthentication(userCredentials)
        }
    }

    /**
     * Удаляет данные о сессии из кэша менеджера токенов.
     *
     * @param sessionId идентификатор сессии для удаления
     */
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
            accessToken = AccessToken(response.accessToken),
            refreshToken = RefreshToken(response.refreshToken),
            issuedAt = now,
            expiresAt = expiresAt
        )
    }

    companion object {
        /**
         * Пустой TokenManager по умолчанию (не используется в продакшене).
         */
        val NONE: TokenManager = TokenManager(TokenRefreshApi.NONE)
    }
}

/**
 * Абстракция для API обновления токена.
 * Позволяет отделить логику TokenManager от HTTP-клиента.
 */
fun interface TokenRefreshApi {
    /**
     * Выполняет запрос на обновление токена.
     *
     * @param accessToken текущий access token
     * @param refreshToken refresh token для обновления
     * @return ответ от сервера с новыми токенами
     */
    suspend fun refreshToken(accessToken: AccessToken, refreshToken: RefreshToken): LoginResponse

    companion object {
        /**
         * Пустая реализация по умолчанию (всегда выбрасывает исключение).
         */
        val NONE: TokenRefreshApi = TokenRefreshApi { _, _ ->
            throw IllegalStateException("TokenRefreshApi.NONE не должен использоваться в продакшене")
        }
    }
}
