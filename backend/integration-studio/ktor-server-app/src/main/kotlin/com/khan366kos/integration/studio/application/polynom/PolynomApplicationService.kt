package com.khan366kos.integration.studio.application.polynom

import com.khan366kos.common.models.auth.SessionId
import com.khan366kos.common.models.business.Catalog
import com.khan366kos.common.models.business.Element
import com.khan366kos.common.polynom.models.Reference
import com.khan366kos.common.models.business.elementGroup.ElementGroup
import com.khan366kos.common.models.PropertyResult
import com.khan366kos.common.models.PropertyValue
import com.khan366kos.common.requests.CreateElementRequest
import com.khan366kos.common.responses.ElementResponse
import com.khan366kos.integration.studio.ktor.server.app.dto.EnrichedSearchResultItem
import com.khan366kos.integration.studio.transport.polynom.response.IPropertyOwnerResponse
import com.khan366kos.etl.polynom.bff.PolynomApi
import com.khan366kos.etl.polynom.bff.auth.AuthProvider
import com.khan366kos.etl.polynom.bff.auth.LoginRequest
import com.khan366kos.etl.polynom.bff.auth.LoginResponse
import com.khan366kos.integration.studio.transport.models.ParentGroup
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceResponse
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.request.GroupRequestDto
import com.khan366kos.integration.studio.transport.polynom.request.IPropertySearchRequest
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import com.khan366kos.integration.studio.transport.polynom.response.AppointedConceptsDto
import com.khan366kos.integration.studio.transport.polynom.response.IPropertySearchResultObject
import com.khan366kos.integration.studio.transport.polynom.response.IPropertySearchResultObjectIPaginatedList
import io.ktor.client.statement.HttpResponse
import jdk.jfr.internal.OldObjectSample.emit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlin.system.measureTimeMillis

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
    suspend fun reference(sessionId: String, request: IIdentifiableObject): Reference {
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

    suspend fun catalogs(sessionId: String, request: IIdentifiableObject): List<Catalog> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.catalogs(authContext, request)
    }

    suspend fun catalog(sessionId: String, request: IIdentifiableObject): Catalog {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.catalog(authContext, request)
    }

    suspend fun groupsByCatalog(sessionId: String, request: IIdentifiableObject): List<ElementGroup> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return try {
            println("try")
            polynomApi.groupsByCatalog(authContext, request)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Получает группы элементов по группе.
     * 
     * @param sessionId идентификатор сессии
     * @param request идентификатор группы
     * @return список групп
     */
    suspend fun groupsByGroup(sessionId: String, request: IIdentifiableObject): List<ElementGroup> {
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
    suspend fun elements(sessionId: String, request: IIdentifiableObject): List<Element> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.elements(authContext, request)
    }

    suspend fun getProperties(sessionId: String, request: OwnerRequest): IPropertyOwnerResponse {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.getProperties(authContext, request)
    }

    suspend fun getPropertiesEnriched(sessionId: String, request: OwnerRequest): List<PropertyResult> {
        val response = getProperties(sessionId, request)

        val valueIndex = mutableMapOf<Pair<Int, Int>, PropertyValue>()

        response.values.stringProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.StringVal(value.value ?: "Unknown", value.typeId, value.objectId)
        }

        response.values.dateTimeProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.DateTimeVal(value.value.value, value.typeId, value.objectId)
        }

        response.values.booleanProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.BooleanVal(value.value ?: false, value.typeId, value.objectId)
        }

        response.values.setProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.SetVal(value.value ?: "Unknown", value.typeId, value.objectId)
        }

        response.values.enumProperties?.forEach { value ->
            valueIndex[value.typeId to value.objectId] =
                PropertyValue.EnumVal(value.value ?: "Unknown", value.typeId, value.objectId)
        }

        return response.propertyOwner.properties?.map { property ->
            val key = property.value?.typeId to property.value?.objectId
            val propertyValue =
                valueIndex[key] ?: PropertyValue.UnknownVal(typeId = property.typeId, objectId = property.objectId)
            PropertyResult(
                name = property.name ?: "Unknown",
                value = propertyValue,
                typeId = property.typeId,
                objectId = property.objectId
            )
        } ?: emptyList()
    }


    suspend fun create(sessionId: String, request: ParentGroup): String {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.createElement(authContext, request)
    }

    suspend fun conceptGetByConceptAppointer(sessionId: String, request: GroupRequestDto): AppointedConceptsDto {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        return polynomApi.conceptGetByConceptAppointer(authContext, request.group)
    }

    suspend fun executePropertySearch(
        sessionId: String,
        request: IPropertySearchRequest
    ): IPropertySearchResultObjectIPaginatedList {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        val result = polynomApi.executePropertySearch(authContext, request)
        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun searchObjects(
        sessionId: String,
        request: IPropertySearchRequest
    ): Flow<List<PropertyResult>> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))

        return flow {
            var page = 1
            var hasNextPage = true
            var totalItems = 0

            while (hasNextPage) {
                val response = polynomApi.executePropertySearch(authContext, request.copy(pageNumber = page))
                response.items?.forEach {
                    totalItems++
                    emit(it)
                }
                hasNextPage = response.hasNextPage
                page++
            }
        }.flatMapMerge(concurrency = 6) { obj ->
            flow {
                val props = getPropertiesEnriched(
                    sessionId,
                    OwnerRequest(owner = IIdentifiableObject(obj.objectId, obj.typeId))
                )
                emit(props)
            }
        }
    }

    suspend fun searchChangedObjects(
        sessionId: String,
        request: IPropertySearchRequest
    ): List<EnrichedSearchResultItem> = coroutineScope {
        val searchResult = executePropertySearch(sessionId, request)
        searchResult.items?.map { item ->
            async {
                val props = getPropertiesEnriched(
                    sessionId,
                    OwnerRequest(IIdentifiableObject(item.objectId, item.typeId))
                )
                EnrichedSearchResultItem(
                    name = item.name,
                    objectId = item.objectId,
                    typeId = item.typeId,
                    iconCode = item.iconCode,
                    properties = props
                )
            }
        }?.awaitAll() ?: emptyList()
    }
}
