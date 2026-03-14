package com.khan366kos.common.models.auth

/**
 * Идентификатор сессии пользователя.
 * Используется для отслеживания аутентифицированных сессий в системе.
 */
@JvmInline
value class SessionId(val value: String) {
    /**
     * Возвращает строковое представление идентификатора сессии.
     */
    fun asString(): String = value

    companion object {
        /**
         * Пустой идентификатор сессии по умолчанию.
         */
        val NONE = SessionId("")
    }
}
