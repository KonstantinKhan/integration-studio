package com.khan366kos.common.models.auth

data class AuthorizationCredentials(
    val username: String,
    val password: String,
    val storageId: String
)