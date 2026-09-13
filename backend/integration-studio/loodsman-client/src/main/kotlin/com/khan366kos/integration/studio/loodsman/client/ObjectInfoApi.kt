package com.khan366kos.integration.studio.loodsman.client

import com.khan366kos.integration.studio.transport.loodsman.models.LinkAttributeDto
import com.khan366kos.integration.studio.transport.loodsman.models.LinkedObjectDto
import com.khan366kos.integration.studio.transport.loodsman.models.ObjectAttributeDto
import com.khan366kos.integration.studio.transport.loodsman.models.PropObjectDto
import com.khan366kos.integration.studio.transport.loodsman.models.VersionListItemDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode

class ObjectInfoApi(private val httpClient: HttpClient) {

    suspend fun getPropObjects(
        sessionId: String,
        dbName: String,
        objectList: String
    ): List<PropObjectDto> {
        val response = httpClient.get("ObjectInfo/get-prop-objects") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
            url {
                parameters.append("objectList", objectList)
            }
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw LoodsmanUnauthorizedException("Loodsman session rejected: HTTP ${response.status.value}")
        }
        return response.body()
    }

    suspend fun getVersionList(
        sessionId: String,
        dbName: String,
        typeName: String?,
        productName: String?
    ): List<VersionListItemDto> {
        val response = httpClient.get("ObjectInfo/get-version-list") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
            url {
                typeName?.let { parameters.append("typeName", it) }
                productName?.let { parameters.append("productName", it) }
            }
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw LoodsmanUnauthorizedException("Loodsman session rejected: HTTP ${response.status.value}")
        }
        return response.body()
    }

    suspend fun getInfoAboutVersionMode3(
        sessionId: String,
        dbName: String,
        idVersion: Int
    ): List<ObjectAttributeDto> {
        val response = httpClient.get("ObjectInfo/get-info-about-version-mode-3") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
            url {
                parameters.append("idVersion", idVersion.toString())
            }
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw LoodsmanUnauthorizedException("Loodsman session rejected: HTTP ${response.status.value}")
        }
        return response.body()
    }

    suspend fun getLObjs(
        sessionId: String,
        dbName: String,
        versionId: Int,
        inverse: Boolean = false
    ): List<LinkedObjectDto> {
        val response = httpClient.get("ObjectInfo/get-l-objs") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
            url {
                parameters.append("versionId", versionId.toString())
                parameters.append("inverse", inverse.toString())
            }
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw LoodsmanUnauthorizedException("Loodsman session rejected: HTTP ${response.status.value}")
        }
        return response.body()
    }

    suspend fun getLinkAttributes(
        sessionId: String,
        dbName: String,
        linkId: Int
    ): List<LinkAttributeDto> {
        val response = httpClient.get("ObjectInfo/get-link-attributes") {
            header("web-loodsman-session", sessionId)
            header("x-loodsman-db-name", dbName)
            url {
                parameters.append("linkId", linkId.toString())
            }
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw LoodsmanUnauthorizedException("Loodsman session rejected: HTTP ${response.status.value}")
        }
        return response.body()
    }
}
