package com.khan366kos.integration.studio.application.polynom

import com.khan366kos.common.models.auth.SessionId
import com.khan366kos.common.models.business.Catalog
import com.khan366kos.common.models.business.Element
import com.khan366kos.common.models.business.Owner
import com.khan366kos.common.models.business.Reference
import com.khan366kos.common.models.business.elementGroup.ElementGroup
import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.common.requests.CreateElementRequest
import com.khan366kos.common.requests.PropertyAssignmentRequest
import com.khan366kos.common.responses.ElementResponse
import com.khan366kos.common.responses.PropertyOwnerResponse
import com.khan366kos.etl.polynom.bff.PolynomApi
import com.khan366kos.etl.polynom.bff.auth.AuthProvider
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import com.khan366kos.etl.polynom.bff.auth.LoginResponse
import com.khan366kos.integration.studio.transport.models.IdentifiableObjectTransport
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceResponse
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import io.ktor.client.statement.HttpResponse

/**
 * Application-сервис для работы с Polynom API.
 * 
 * Этот класс оркестрирует вызовы между AuthProvider и PolynomApi,
 * обеспечивая единую точку входа для использования Polynom API.
 * 
 * @param authProvider провайдер аутентификации для получения credentials
 * @param polynomApi HTTP-адаптер для вызовов Polynom API
 */
class PolynomApplicationService(
    private val authProvider: AuthProvider,
    private val polynomApi: PolynomApi
) {
    
    // ==================== Authentication ====================
    
    /**
     * Получает список определений хранилищ (не требует аутентификации).
     */
    suspend fun storageDefinitions(): List<StorageDefinitionTransport> =
        polynomApi.storageDefinitions()
    
    /**
     * Выполняет вход пользователя.
     * 
     * @return ответ с токенами доступа
     */
    suspend fun signIn(loginRequest: LoginRequest): LoginResponse =
        polynomApi.signIn(loginRequest)
    
    /**
     * Получает информацию о текущем пользователе.
     * 
     * @param sessionId идентификатор сессии
     * @return информация о пользователе
     */
    suspend fun currentUserInfo(sessionId: String): UserTransport {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.currentUserInfo(authContext)
    }
    
    // ==================== References ====================
    
    /**
     * Получает все справочники.
     * 
     * @param sessionId идентификатор сессии
     * @return список справочников
     */
    suspend fun references(sessionId: String): List<Reference> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.references(authContext)
    }
    
    /**
     * Получает справочник по идентификатору.
     * 
     * @param sessionId идентификатор сессии
     * @param request идентификатор справочника
     * @return справочник
     */
    suspend fun reference(sessionId: String, request: IdentifiableObjectTransport): Reference {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.reference(authContext, request)
    }

    suspend fun referenceCreate(sessionId: String, request: CreateReferenceCommand): CreateReferenceResponse {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.referenceCreate(authContext, request)
    }

    suspend fun referenceDelete(sessionId: String, request: DeleteReferenceCommand): HttpResponse {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.referenceDelete(authContext, request)
    }

    suspend fun catalogs(sessionId: String, request: IdentifiableObjectTransport): List<Catalog> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.catalogs(authContext, request)
    }

    suspend fun catalog(sessionId: String, request: IdentifiableObjectTransport): Catalog {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.catalog(authContext, request)
    }

    suspend fun groupsByCatalog(sessionId: String, request: IdentifiableObjectTransport): List<ElementGroup> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.groupsByCatalog(authContext, request)
    }
    
    /**
     * Получает группы элементов по группе.
     * 
     * @param sessionId идентификатор сессии
     * @param request идентификатор группы
     * @return список групп
     */
    suspend fun groupsByGroup(sessionId: String, request: IdentifiableObjectTransport): List<ElementGroup> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.groupsByGroup(authContext, request)
    }
    
    // ==================== Elements ====================
    
    /**
     * Создаёт новый элемент.
     * 
     * @param sessionId идентификатор сессии
     * @param request команда на создание элемента
     * @return результат создания
     */
    suspend fun element(sessionId: String, request: CreateElementRequest): ElementResponse {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.element(authContext, request)
    }
    
    /**
     * Получает элементы по группе.
     * 
     * @param sessionId идентификатор сессии
     * @param request идентификатор группы
     * @return список элементов
     */
    suspend fun elements(sessionId: String, request: IdentifiableObjectTransport): List<Element> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.elements(authContext, request)
    }
    
    // ==================== Properties ====================
    
    /**
     * Получает свойства владельца.
     * 
     * @param sessionId идентификатор сессии
     * @param request владелец (тип + объект)
     * @return свойства владельца
     */
    suspend fun getProperties(sessionId: String, request: Owner): PropertyOwnerResponse {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.getProperties(authContext, request)
    }
    
    /**
     * Устанавливает значения свойств владельца.
     * 
     * @param sessionId идентификатор сессии
     * @param request назначение свойств
     * @return результат операции
     */
    suspend fun setPropertyValues(sessionId: String, request: PropertyAssignmentRequest): String {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.setPropertyValues(authContext, request)
    }
}
