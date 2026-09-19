package com.khan366kos.integration.studio.ktor.server.app.errors

/**
 * Thrown when an endpoint depends on a service that is not connected yet.
 * Mapped to HTTP 503 by StatusPages.
 */
class ServiceUnavailableException(
    val service: String,
    detail: String? = null,
) : RuntimeException(detail ?: "Service '$service' is not available")
