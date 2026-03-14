package com.khan366kos.etl.polynom.bff.auth

import com.khan366kos.common.models.auth.AuthContext
import com.khan366kos.common.models.auth.SessionId

/**
 * Порт для получения и обновления контекста аутентификации.
 * 
 * Этот интерфейс определяет контракт для инфраструктуры аутентификации,
 * позволяя отделять domain-логику от деталей реализации (SessionStore, HTTP и т.д.).
 * 
 * @see SessionStoreAuthProvider реализация по умолчанию
 */
interface AuthProvider {
    /**
     * Получает контекст аутентификации для указанной сессии.
     * 
     * @param sessionId идентификатор сессии
     * @return контекст аутентификации с учётными данными
     * @throws AuthenticationException.SessionNotFound если сессия не найдена в хранилище
     * @throws AuthenticationException.InvalidCredentials если учётные данные некорректны
     */
    suspend fun getAuthContext(sessionId: SessionId): AuthContext
    
    /**
     * Обновляет учётные данные сессии (refresh token).
     * 
     * @param sessionId идентификатор сессии
     * @return обновлённый контекст аутентификации
     * @throws AuthenticationException.TokenExpired если refresh token истёк
     * @throws AuthenticationException.SessionNotFound если сессия не найдена
     */
    suspend fun refreshAuth(sessionId: SessionId): AuthContext
}
