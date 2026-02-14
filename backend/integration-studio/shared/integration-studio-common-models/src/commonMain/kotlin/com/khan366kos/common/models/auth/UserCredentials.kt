package com.khan366kos.common.models.auth

import com.khan366kos.common.models.auth.simple.AccessToken
import com.khan366kos.common.models.auth.simple.Login
import com.khan366kos.common.models.auth.simple.RefreshToken
import com.khan366kos.common.models.auth.simple.StorageId

data class UserCredentials(
    val login: Login = Login.NONE,
    val storageId: StorageId = StorageId.NONE,
    val accessToken: AccessToken = AccessToken.NONE,
    val refreshToken: RefreshToken = RefreshToken.NONE,
    val issuedAt: Long = 0L,
    val expiresAt: Long = 0L
) {
    fun isInvalidAccessToken() = accessToken == AccessToken.NONE
}