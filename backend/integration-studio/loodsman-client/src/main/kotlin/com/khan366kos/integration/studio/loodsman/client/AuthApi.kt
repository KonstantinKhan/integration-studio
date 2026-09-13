package com.khan366kos.integration.studio.loodsman.client

import com.khan366kos.integration.studio.transport.loodsman.models.DatabaseOutputDto
import com.khan366kos.integration.studio.transport.loodsman.models.LoginInputDto
import com.khan366kos.integration.studio.transport.loodsman.models.SessionOutputDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(private val httpClient: HttpClient) {

    suspend fun databases(): List<DatabaseOutputDto> =
        httpClient.get("Auth/databases").body()

    suspend fun login(input: LoginInputDto): SessionOutputDto =
        httpClient.post("Auth/login") {
            contentType(ContentType.Application.Json)
            setBody(input)
        }.body()

    suspend fun logout(sessionId: String, dbName: String) {
        httpClient.post("Auth/logout") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
        }
    }
}
