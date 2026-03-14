package com.khan366kos.etl.polynom.bff.config

object AuthConfig {
    const val REFRESH_THRESHOLD = 0.8
    const val LOGIN_ENDPOINT = "/login/update-token"
    
    /**
     * Базовый URL Polynom API.
     * Используется PolynomApi и SessionStoreAuthProvider.
     */
    const val BASE_URL = "https://delusively-altruistic-pangolin.cloudpub.ru:443"
    const val BASE_API_PATH = "/api/v1"
}