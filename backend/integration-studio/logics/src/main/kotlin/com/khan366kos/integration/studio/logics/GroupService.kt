package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.integration.studio.mapping.toDomain
import com.khan366kos.integration.studio.polynom.client.PolynomApi

class GroupService(private val polynomApi: PolynomApi) {
    suspend fun create(sessionId: String, typeId: Int, objectId: Int, name: String): ClassifierTreeNode.Group =
        polynomApi.groupApi.create(sessionId, typeId, objectId, name).toDomain()
}