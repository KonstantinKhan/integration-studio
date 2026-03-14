package com.khan366kos.common.models.auth

/**
 * Контекст аутентификации, содержащий информацию о сессии и учётных данных пользователя.
 * Используется в domain-слое для передачи аутентификационной информации.
 */
data class AuthContext(
    val sessionId: SessionId,
    val credentials: UserCredentials
) {
    companion object {
        /**
         * Пустой контекст аутентификации по умолчанию.
         */
        val NONE = AuthContext(
            sessionId = SessionId.NONE,
            credentials = UserCredentials()
        )
    }
}
