package com.khan366kos.integration.studio.loodsman.client

import com.khan366kos.integration.studio.transport.loodsman.models.PdmNodeDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode

class LoodsmanUnauthorizedException(message: String) : RuntimeException(message)

class PdmApi(private val httpClient: HttpClient) {

    suspend fun getTree(
        sessionId: String,
        dbName: String,
        idVersion: Int? = null,
        hasLink: Boolean? = null,
        linkTypes: String? = null,
        direction: Int? = null
    ): List<PdmNodeDto> {
        val response = httpClient.get("Pdm/get-tree") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
            url {
                idVersion?.let { parameters.append("idVersion", it.toString()) }
                hasLink?.let { parameters.append("hasLink", it.toString()) }
                linkTypes?.let { parameters.append("linkTypes", it) }
                direction?.let { parameters.append("direction", it.toString()) }
            }
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw LoodsmanUnauthorizedException("Loodsman session rejected: HTTP ${response.status.value}")
        }
        return response.body()
    }
}
