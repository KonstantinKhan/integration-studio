package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.polynom.client.PolynomApi

class ReferenceService(private val polynomApi: PolynomApi) {
    suspend fun referenceCreate(sessionId: String, name: String): ClassifierTreeNode.Reference =
        polynomApi.referenceCreate(sessionId, name).toDomain()
}