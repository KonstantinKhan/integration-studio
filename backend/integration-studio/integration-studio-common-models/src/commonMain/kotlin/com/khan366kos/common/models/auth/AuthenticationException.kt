package com.khan366kos.common.models.auth

/**
 * Базовый класс для исключений аутентификации.
 * Используется для обработки ошибок, связанных с аутентификацией и сессиями.
 */
sealed class AuthenticationException(message: String) : Exception(message) {
    /**
     * Исключение, возникающее при попытке использовать несуществующую сессию.
     * @param sessionId идентификатор сессии, которая не была найдена
     */
    class SessionNotFound(sessionId: String) : AuthenticationException("Сессия не найдена: $sessionId")

    /**
     * Исключение, возникающее при истечении срока действия токена.
     * @param sessionId идентификатор сессии с истёкшим токеном
     */
    class TokenExpired(sessionId: String) : AuthenticationException("Токен истёк: $sessionId")

    /**
     * Исключение, возникающее при предоставлении неверных учётных данных.
     */
    class InvalidCredentials : AuthenticationException("Неверные учётные данные")

    companion object {
        /**
         * Пустое исключение по умолчанию (не используется для выбрасывания).
         */
        val NONE: AuthenticationException? = null
    }
}
