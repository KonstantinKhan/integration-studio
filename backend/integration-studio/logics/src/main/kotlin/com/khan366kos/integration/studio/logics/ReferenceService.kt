package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.polynom.client.PolynomClient

class ReferenceService(private val polynomClient: PolynomClient) {
    suspend fun referenceCreate(sessionId: String, name: String): ClassifierTreeNode.Reference =
        polynomClient.referenceCreate(sessionId, name).toDomain()
}