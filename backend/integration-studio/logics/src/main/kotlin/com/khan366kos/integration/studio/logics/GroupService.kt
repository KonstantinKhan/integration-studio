package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.polynom.client.PolynomClient

class GroupService(private val polynomClient: PolynomClient) {
    suspend fun create(sessionId: String, typeId: Int, objectId: Int, name: String): ClassifierTreeNode.Group =
        polynomClient.groupApi.create(sessionId, typeId, objectId, name).toDomain()
}