package com.khan366kos.integration.studio.application.polynom

import com.khan366kos.domain.polynom.Identifier
import com.khan366kos.domain.polynom.Node
import com.khan366kos.domain.models.auth.SessionId
import com.khan366kos.domain.models.business.Catalog
import com.khan366kos.domain.models.business.Element
import com.khan366kos.domain.polynom.models.Reference
import com.khan366kos.domain.models.business.elementGroup.ElementGroup
import com.khan366kos.domain.polynom.PolynomElement
import com.khan366kos.domain.polynom.PropertyResult
import com.khan366kos.domain.polynom.Property
import com.khan366kos.domain.polynom.PropertyValueSimple
import com.khan366kos.domain.polynom.toSimple
import com.khan366kos.domain.requests.CreateElementRequest
import com.khan366kos.domain.responses.ElementResponse
import com.khan366kos.integration.studio.transport.polynom.response.IPropertyOwnerResponse
import com.khan366kos.etl.polynom.bff.PolynomApi
import com.khan366kos.etl.polynom.bff.auth.AuthProvider
import com.khan366kos.integration.studio.bff.transport.request.ElementFromPeriodRequestBffDto
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.mapping.toPolynomDto
import com.khan366kos.integration.studio.transport.polynom.models.LoginRequest
import com.khan366kos.integration.studio.transport.polynom.models.LoginResponse
import com.khan366kos.integration.studio.transport.models.ParentGroup
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.command.CreateReferenceResponse
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.request.GroupRequestDto
import com.khan366kos.integration.studio.transport.polynom.request.IClassificationNodeChildrenRequest
import com.khan366kos.integration.studio.transport.polynom.request.IClassificationTreeRequest
import com.khan366kos.integration.studio.transport.polynom.request.IPropertySearchRequest
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import com.khan366kos.integration.studio.transport.polynom.response.AppointedConceptsDto
import com.khan366kos.integration.studio.transport.polynom.response.IPropertySearchResultObjectIPaginatedList
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

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

    suspend fun polynomElement(sessionId: String, request: OwnerRequest): PolynomElement {
        val response = getProperties(sessionId, request)

        val valueIndex = mutableMapOf<Pair<Int, Int>, Property>()

        with(response.values) {
            stringProperties?.forEach { value ->
                valueIndex[value.typeId to value.objectId] =
                    Property.StringVal(value.value ?: "Unknown", value.typeId, value.objectId)
            }

            dateTimeProperties?.forEach { value ->
                valueIndex[value.typeId to value.objectId] =
                    Property.DateTimeVal(value.value.value, value.typeId, value.objectId)
            }

            booleanProperties?.forEach { value ->
                valueIndex[value.typeId to value.objectId] =
                    Property.BooleanVal(value.value ?: false, value.typeId, value.objectId)
            }

            setProperties?.forEach { value ->
                valueIndex[value.typeId to value.objectId] =
                    Property.SetVal(value.value ?: "Unknown", value.typeId, value.objectId)
            }

            enumProperties?.forEach { value ->
                valueIndex[value.typeId to value.objectId] =
                    Property.EnumVal(value.value ?: "Unknown", value.typeId, value.objectId)
            }
        }

        val properties = response.propertyOwner.properties?.map { property ->
            val key = property.value?.typeId to property.value?.objectId
            val propertyValue =
                valueIndex[key]?.toSimple() ?: PropertyValueSimple.UnknownValSimple("Неизвестный тип")
            PropertyResult(
                name = property.name ?: "Unknown",
                value = propertyValue,
                typeId = property.typeId,
                objectId = property.objectId
            )
        } ?: emptyList()

        val element = PolynomElement(
            name = when (val value = properties.find { it.name == "Наименование" }?.value) {
                is PropertyValueSimple.StringValSimple -> value.data
                else -> throw NotImplementedError()
            },
            properties = properties.filter { it.name != "Наименование" },
            typeId = response.propertyOwner.typeId,
            objectId = response.propertyOwner.objectId,
        )
        return element
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
        request: ElementFromPeriodRequestBffDto
    ): Flow<PolynomElement> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))

        return flow {
            var page = 1
            var hasNextPage = true
            var totalItems = 0
            val polynomRequest = request.toPolynomDto()

            while (hasNextPage) {
                val response = polynomApi.executePropertySearch(authContext, polynomRequest.copy(pageNumber = page))
                response.items?.forEach {
                    totalItems++
                    emit(it)
                }
                hasNextPage = response.hasNextPage
                page++
            }
        }.flatMapMerge(concurrency = 6) { obj ->
            flow {
                val props = polynomElement(
                    sessionId,
                    OwnerRequest(owner = IIdentifiableObject(obj.objectId, obj.typeId))
                )
                emit(props)
            }
        }
    }

    /**
     * Вариант [searchObjects], эмитящий на выходе обогащённый объект
     * (с полями объекта + его свойствами). Используется SSE-стримингом миграции.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun searchObjectsEnriched(
        sessionId: String,
        request: ElementFromPeriodRequestBffDto
    ): Flow<PolynomElement> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        val requestPolynom = request.toPolynomDto()
        return flow {
            var page = 1
            var hasNextPage = true

            while (hasNextPage) {
                val response = polynomApi.executePropertySearch(authContext, requestPolynom.copy(pageNumber = page))
                response.items?.forEach { emit(it) }
                hasNextPage = response.hasNextPage
                page++
            }
        }.flatMapMerge(concurrency = 6) { obj ->
            flow {
                val element = polynomElement(
                    sessionId,
                    OwnerRequest(owner = IIdentifiableObject(obj.objectId, obj.typeId))
                )
                emit(
                    element
                )
            }
        }
    }

    suspend fun searchChangedObjects(
        sessionId: String,
        request: IPropertySearchRequest
    ): List<PolynomElement> = coroutineScope {
        val searchResult = executePropertySearch(sessionId, request)
        searchResult.items?.map { item ->
            async {
                val element = polynomElement(
                    sessionId,
                    OwnerRequest(IIdentifiableObject(item.objectId, item.typeId))
                )
               element
            }
        }?.awaitAll() ?: emptyList()
    }

    suspend fun getClassification(
        sessionId: String,
    ): List<Node> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        val response = polynomApi.getClassification(authContext, IClassificationTreeRequest.Root)
        val result = response.items.map { it.toDomain() }
        return result
    }

    suspend fun nodes(
        sessionId: String,
        identifier: Identifier
    ): List<Node> {
        val authContext = authProvider.getAuthContext(SessionId(sessionId))
        val request = IClassificationNodeChildrenRequest(
            pageNumber = 1, pageSize = 25, parentNodeObject = IIdentifiableObject(
                typeId = identifier.typeId.asInt(),
                objectId = identifier.objectId.asInt(),
            )
        )
        val response = polynomApi.getClassificationNodeChildren(authContext, request)
        val result = response.items.map { it.toDomain() }
        return result
    }
}
