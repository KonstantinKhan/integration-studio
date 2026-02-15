package com.khan366kos.etl.polynom.bff.auth

import io.ktor.client.plugins.api.*
import io.ktor.http.*

import com.khan366kos.etl.polynom.bff.PolynomClient

class AuthPluginConfig {
    var baseUrl: String = ""
    lateinit var tokenManager: TokenManager
    lateinit var polynomClient: PolynomClient
}

val AuthPlugin = createClientPlugin("AuthPlugin", ::AuthPluginConfig) {
    val tokenManager = pluginConfig.tokenManager
    val baseUrl = pluginConfig.baseUrl
    val polynomClient = pluginConfig.polynomClient

    on(Send) { request ->
        val url = request.url.toString()
        if (url.contains("login/sign-in")
            || url.contains("storage-definitions")
            || url.contains("update-token")
        ) {
            return@on proceed(request)
        }

        val credentials = request.attributes.getOrNull(UserCredentialsAttrKey)
            ?: return@on proceed(request)
        val sessionId = request.attributes.getOrNull(SessionIdAttrKey)
            ?: return@on proceed(request)

        val validCredentials = tokenManager.getValidCredentials(sessionId, credentials, client, baseUrl)

        if (validCredentials != credentials) {
            polynomClient._credentialsUpdates.tryEmit(Pair(sessionId, validCredentials))
        }

        request.headers.remove(HttpHeaders.Authorization)
        request.headers.append(HttpHeaders.Authorization, "Bearer ${validCredentials.accessToken.value}")

        val originalCall = proceed(request)

        if (originalCall.response.status == HttpStatusCode.Unauthorized) {
            val refreshedCredentials = tokenManager.authenticate(sessionId, validCredentials, client, baseUrl)

            request.headers.remove(HttpHeaders.Authorization)
            request.headers.append(HttpHeaders.Authorization, "Bearer ${refreshedCredentials.accessToken.value}")

            proceed(request)
        } else {
            originalCall
        }
    }
}