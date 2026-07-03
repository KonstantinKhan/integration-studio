package com.khan366kos.integration.studio.polynom.client

import com.khan366kos.integration.studio.polynom.client.auth.SessionStoreAuthProvider
import com.khan366kos.integration.studio.polynom.client.auth.TokenManager
import com.khan366kos.integration.studio.transport.polynom.models.catalog.IElementCatalog
import com.khan366kos.integration.studio.transport.polynom.models.concept.IConcept
import com.khan366kos.integration.studio.transport.polynom.request.IIdentifierRequest
import com.khan366kos.integration.studio.transport.polynom.request.concept.IAddAppointedConceptRequest
import com.khan366kos.integration.studio.transport.polynom.request.concept.IGetAllConceptsRequest
import com.khan366kos.integration.studio.transport.polynom.request.concept.IGetByCodeRequest
import com.khan366kos.integration.studio.transport.polynom.response.concept.IAppointedConceptsResponse
import com.khan366kos.integration.studio.transport.polynom.response.concept.IConceptPaginatedList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

class ConceptApi(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager,
    private val authProvider: SessionStoreAuthProvider
) {
    suspend fun getAll(sessionId: String, request: IGetAllConceptsRequest): IConceptPaginatedList =
        httpClient.post("concept/get-all") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(request)
        }.body()

    suspend fun getByCode(sessionId: String, code: String): IConcept =
        httpClient.post("concept/get-by-code") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(IGetByCodeRequest(code))
        }.body()

    suspend fun addAppointedConcept(
        sessionId: String,
        objectIdAppointed: Int,
        typeIdAppointed: Int,
        objectIdConcept: Int,
        typeIdConcept: Int
    ): IAppointedConceptsResponse =
        httpClient.post("concept/add-appointed-concept") {
            authenticate(authProvider.getAuthContext(SessionId(sessionId)))
            setBody(
                IAddAppointedConceptRequest(
                    IIdentifierRequest(objectIdAppointed, typeIdAppointed),
                    IIdentifierRequest(objectIdConcept, typeIdConcept),
                )
            )
        }.body()

    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }

}