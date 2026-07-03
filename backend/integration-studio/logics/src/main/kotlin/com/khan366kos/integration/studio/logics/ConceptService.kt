package com.khan366kos.integration.studio.logics

import com.khan366kos.integration.studio.polynom.client.PolynomApi

class ConceptService(
    private val polynomApi: PolynomApi
) {
    suspend fun addConceptToCatalog(
        sessionId: String,
        typeIdAppointed: Int,
        objectIdAppointed: Int,
        typeIdConcept: Int,
        objectIdConcept: Int
    ) {
        polynomApi.conceptApi.addAppointedConcept(
            sessionId,
            objectIdAppointed,
            typeIdAppointed,
            objectIdConcept,
            typeIdConcept
        )
    }
}