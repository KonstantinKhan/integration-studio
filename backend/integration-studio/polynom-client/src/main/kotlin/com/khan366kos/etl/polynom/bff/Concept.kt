package com.khan366kos.etl.polynom.bff

import com.khan366kos.domain.models.auth.AuthContext
import com.khan366kos.etl.polynom.bff.auth.TokenManager
import com.khan366kos.integration.studio.transport.polynom.request.concept.IGetAllConceptsRequest
import com.khan366kos.integration.studio.transport.polynom.response.concept.IConceptPaginatedList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

class Concept(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) {
    suspend fun getAll(authContext: AuthContext, request: IGetAllConceptsRequest): IConceptPaginatedList =
        httpClient.post("concept/get-all") {
            authenticate(authContext)
            setBody(request)
        }.body()

    private suspend fun HttpRequestBuilder.authenticate(authContext: AuthContext) {
        val validCredentials = tokenManager.getValidCredentials(
            sessionId = authContext.sessionId.value,
            userCredentials = authContext.credentials
        )
        header(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")
    }
}