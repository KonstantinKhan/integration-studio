package com.khan366kos.domain.models.auth

data class AuthorizationCredentials(
    val username: String,
    val password: String,
    val storageId: String
)