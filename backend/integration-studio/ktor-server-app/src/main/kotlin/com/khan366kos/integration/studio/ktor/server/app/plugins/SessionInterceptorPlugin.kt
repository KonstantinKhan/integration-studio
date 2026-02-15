package com.khan366kos.integration.studio.ktor.server.app.plugins

import com.khan366kos.common.models.auth.UserCredentials
import com.khan366kos.integration.studio.ktor.server.app.UserSession
import com.khan366kos.integration.studio.ktor.server.app.session.SessionStore
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.AttributeKey

val UserSessionKey = AttributeKey<UserSession>("UserSession")
val UserCredentialsKey = AttributeKey<UserCredentials>("UserCredentials")

val ApplicationCall.userSession: UserSession
    get() = attributes[UserSessionKey]

val ApplicationCall.userCredentials: UserCredentials
    get() = attributes[UserCredentialsKey]

class SessionInterceptorConfig {
    lateinit var sessionStore: SessionStore
}

val SessionInterceptorPlugin = createRouteScopedPlugin(
    "SessionInterceptor",
    ::SessionInterceptorConfig
) {
    val sessionStore = pluginConfig.sessionStore

    onCall { call ->
        val session = call.sessions.get<UserSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Сессия не найдена"))
            return@onCall
        }

        val credentials = sessionStore.retrieve(session.id)
        if (credentials == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Учётные данные не найдены"))
            return@onCall
        }

        call.attributes.put(UserSessionKey, session)
        call.attributes.put(UserCredentialsKey, credentials)
    }
}