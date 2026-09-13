package com.khan366kos.integration.studio.loodsman.session

data class LoodsmanSession(
    val sessionId: String,
    val dbName: String,
    val userId: Int,
    val username: String
)
