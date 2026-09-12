package com.khan366kos.integration.studio.logics

import com.khan366kos.integration.studio.polynom.client.PolynomClient

class ConceptService(
    private val polynomClient: PolynomClient
) {
    suspend fun addConceptToCatalog(
        sessionId: String,
        typeIdAppointed: Int,
        objectIdAppointed: Int,
        typeIdConcept: Int,
        objectIdConcept: Int
    ) {
        polynomClient.conceptApi.addAppointedConcept(
            sessionId,
            objectIdAppointed,
            typeIdAppointed,
            objectIdConcept,
            typeIdConcept
        )
    }
}