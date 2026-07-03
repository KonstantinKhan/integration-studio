package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.models.business.Element
import com.khan366kos.domain.models.business.elementGroup.ElementGroup
import com.khan366kos.domain.polynom.Identifier
import com.khan366kos.domain.polynom.Node
import com.khan366kos.domain.polynom.PolynomElement
import com.khan366kos.domain.polynom.Property
import com.khan366kos.domain.polynom.PropertyResult
import com.khan366kos.domain.polynom.PropertyValueSimple
import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.domain.polynom.models.Concept
import com.khan366kos.domain.polynom.toSimple
import com.khan366kos.domain.requests.CreateElementRequest
import com.khan366kos.domain.responses.ElementResponse
import com.khan366kos.integration.studio.polynom.client.PolynomApi
import com.khan366kos.integration.studio.bff.dto.request.PolynomElementFromPeriodRequestBffDto
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.mapping.toPolynomDto
import com.khan366kos.integration.studio.transport.models.StorageDefinitionTransport
import com.khan366kos.integration.studio.transport.models.UserTransport
import com.khan366kos.integration.studio.transport.polynom.command.DeleteReferenceCommand
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.LoginRequest
import com.khan366kos.integration.studio.transport.polynom.models.LoginResponse
import com.khan366kos.integration.studio.transport.polynom.request.GroupRequestDto
import com.khan366kos.integration.studio.transport.polynom.request.IClassificationNodeChildrenRequest
import com.khan366kos.integration.studio.transport.polynom.request.IClassificationTreeRequest
import com.khan366kos.integration.studio.transport.polynom.request.OwnerRequest
import com.khan366kos.integration.studio.transport.polynom.request.search.IPropertySearchRequest
import com.khan366kos.integration.studio.transport.polynom.response.AppointedConceptsDto
import com.khan366kos.integration.studio.transport.polynom.response.IPropertyOwnerResponse
import com.khan366kos.integration.studio.transport.polynom.response.search.IPropertySearchResultObjectIPaginatedList
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlin.collections.get

class PolynomApplicationService(
    private val polynomApi: PolynomApi
) {
    val catalogService = CatalogService(polynomApi)
    val conceptService = ConceptService(polynomApi)
    val groupService = GroupService(polynomApi)

    suspend fun storageDefinitions(): List<StorageDefinitionTransport> =
        polynomApi.storageDefinitions()

    suspend fun signIn(loginRequest: LoginRequest): LoginResponse =
        polynomApi.signIn(loginRequest)

    suspend fun currentUserInfo(sessionId: String): UserTransport {
        return polynomApi.currentUserInfo(sessionId)
    }

    suspend fun references(sessionId: String): List<ClassifierTreeNode.Reference> =
        polynomApi.references(sessionId).map { it.toDomain() }

    suspend fun reference(sessionId: String, request: IIdentifiableObject): ClassifierTreeNode.Reference =
        polynomApi.reference(sessionId, request).toDomain()


    suspend fun referenceCreate(sessionId: String, name: String): ClassifierTreeNode.Reference =
        polynomApi.referenceCreate(sessionId, name).toDomain()

    suspend fun referenceDelete(sessionId: String, request: DeleteReferenceCommand): HttpResponse {
        return polynomApi.referenceDelete(sessionId, request)
    }

    suspend fun catalogs(sessionId: String, typeId: Int, objectId: Int): List<ClassifierTreeNode.Catalog> =
        polynomApi.catalogApi.getByReference(sessionId, typeId, objectId).map { it.toDomain() }

    suspend fun catalog(sessionId: String, typeId: Int, objectId: Int): ClassifierTreeNode.Catalog =
        polynomApi.catalogApi.getById(sessionId, typeId, objectId).toDomain()

    suspend fun groupsByCatalog(sessionId: String, request: IIdentifiableObject): List<ElementGroup> {
        return try {
            polynomApi.groupsByCatalog(sessionId, request)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun groupsByGroup(sessionId: String, request: IIdentifiableObject): List<ElementGroup> {
        return polynomApi.groupsByGroup(sessionId, request)
    }

    suspend fun element(sessionId: String, request: CreateElementRequest): ElementResponse {
        return polynomApi.element(sessionId, request)
    }

    suspend fun elements(sessionId: String, request: IIdentifiableObject): List<Element> {
        return polynomApi.elements(sessionId, request)
    }

    suspend fun getProperties(sessionId: String, request: OwnerRequest): IPropertyOwnerResponse {
        return polynomApi.getProperties(sessionId, request)
    }

    suspend fun polynomElement(sessionId: String, request: OwnerRequest): PolynomElement {
        val response = getProperties(sessionId, request)

        val valueIndex = mutableMapOf<Pair<Int, Int>, Property>()

        with(response.values) {
            stringProperties?.forEach { value ->
                valueIndex[value.typeId to value.objectId] =
                    Property.StringVal(value.value ?: "empty", value.typeId, value.objectId)
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
                valueIndex[key]?.toSimple() ?: PropertyValueSimple.EmptyValSimple("")
            PropertyResult(
                name = property.name ?: "Unknown",
                value = propertyValue,
                typeId = property.typeId,
                objectId = property.objectId
            )
        } ?: emptyList()

        val element = PolynomElement(
            designation = when (val value = properties.find {
                it.name == "Обозначение"
            }?.value) {
                is PropertyValueSimple.StringValSimple -> value.data
                else -> {
                    throw NotImplementedError()
                }
            },
            classifierCode = when (val value = properties.find { it.name == "Код классификатора" }?.value) {
                is PropertyValueSimple.StringValSimple -> value.data.toLong()
                else -> throw NotImplementedError()
            },
            changeDate = when (val value = properties.find { it.name == "Дата последнего изменения" }?.value) {
                is PropertyValueSimple.DateTimeValSimple -> LocalDateTime.parse(value.data)
                else -> throw NotImplementedError()
            },
            properties = properties.filter {
                !listOf(
                    "Обозначение",
                    "Код классификатора",
                    "Дата последнего изменения"
                ).contains(it.name)
            },
            typeId = response.propertyOwner.typeId,
            objectId = response.propertyOwner.objectId,
        )
        return element
    }


    suspend fun create(
        sessionId: String,
        request: com.khan366kos.integration.studio.transport.models.ParentGroup
    ): String {
        return polynomApi.createElement(sessionId, request)
    }

    suspend fun conceptGetByConceptAppointer(sessionId: String, request: GroupRequestDto): AppointedConceptsDto {
        return polynomApi.conceptGetByConceptAppointer(sessionId, request.group)
    }

    suspend fun executePropertySearch(
        sessionId: String,
        request: IPropertySearchRequest
    ): IPropertySearchResultObjectIPaginatedList {
        val result = polynomApi.executePropertySearch(sessionId, request)
        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun searchObjects(
        sessionId: String,
        request: PolynomElementFromPeriodRequestBffDto
    ): Flow<PolynomElement> {

        return flow {
            var page = 1
            var hasNextPage = true
            var totalItems = 0
            val polynomRequest = request.toPolynomDto()

            while (hasNextPage) {
                val response = polynomApi.executePropertySearch(sessionId, polynomRequest.copy(pageNumber = page))
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun searchObjectsEnriched(
        sessionId: String,
        request: PolynomElementFromPeriodRequestBffDto
    ): Flow<PolynomElement> {
        val requestPolynom = request.toPolynomDto()
        return flow {
            var page = 1
            var hasNextPage = true

            while (hasNextPage) {
                val response = polynomApi.executePropertySearch(sessionId, requestPolynom.copy(pageNumber = page))
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
        val response = polynomApi.getClassification(sessionId, IClassificationTreeRequest.Root)
        val result = response.items.map { it.toDomain() }
        return result
    }

    suspend fun nodes(
        sessionId: String,
        identifier: Identifier
    ): List<Node> {
        val request = IClassificationNodeChildrenRequest(
            pageNumber = 1, pageSize = 25, parentNodeObject = IIdentifiableObject(
                typeId = identifier.typeId.asInt(),
                objectId = identifier.objectId.asInt(),
            )
        )
        val response = polynomApi.getClassificationNodeChildren(sessionId, request)
        val result = response.items.map { it.toDomain() }
        return result
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    suspend fun concepts(sessionId: String, concepts: List<String>): List<Concept> =
//        concepts.asFlow()
//            .flatMapMerge(concurrency = 2) { str ->
//                flow {
//                    val request = IGetAllConceptsRequest(
//                        pageNumber = 1, pageSize = 10, filterString = str
//                    )
//                    val result = polynomApi.conceptApi.getAll(sessionId, request)
//                    emit(result)
//                }
//            }.toList()
//            .flatMap { it.items.orEmpty() }
//            .map { it.toDomain() }

    suspend fun concepts(sessionId: String, codes: List<String>): List<Concept> =
        withContext(Dispatchers.IO) {
            codes.map { code ->
                async {
                    polynomApi.conceptApi.getByCode(sessionId, code).toDomain()
                }
            }
        }.awaitAll().toList()
}