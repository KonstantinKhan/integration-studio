package com.khan366kos.integration.studio.loodsman.client

import io.ktor.client.HttpClient

class LoodsmanClient(httpClient: HttpClient) {
    val authApi = AuthApi(httpClient)
    val pdmApi = PdmApi(httpClient)
}
