package com.khan366kos.etl.polynom.bff.auth

import com.khan366kos.common.models.auth.UserCredentials
import io.ktor.util.AttributeKey
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class CredentialsContext(
    val sessionId: String,
    val credentials: UserCredentials
) : AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<CredentialsContext>
}

val UserCredentialsAttrKey = AttributeKey<UserCredentials>("UserCredentials")
val SessionIdAttrKey = AttributeKey<String>("SessionId")