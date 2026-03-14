package com.khan366kos.common.models.business

import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId
import com.khan366kos.common.models.simple.WriteAccess
import kotlinx.serialization.Serializable

@Serializable
data class PropertySource(
    val id: String,
    val absoluteCode: String,
    val isLinked: Boolean,
    val definition: Identifier? = null,
    val ownerContract: Identifier,
    val linkDefinitionEnd: Identifier,
    val linkedConceptPropertySource: Identifier,
    val writeAccess: WriteAccess,
    val objectId: ObjectId,
    val typeId: TypeId,
    val defaultPropertyValue: Identifier? = null,
)
