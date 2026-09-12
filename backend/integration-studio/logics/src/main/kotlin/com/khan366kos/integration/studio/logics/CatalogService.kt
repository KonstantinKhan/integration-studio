package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.polynom.client.PolynomClient

class CatalogService(private val polynomClient: PolynomClient) {
    suspend fun create(sessionId: String, typeId: Int, objectId: Int, name: String): ClassifierTreeNode.Catalog =
        polynomClient.catalogApi.create(sessionId, typeId, objectId, name).toDomain()
}