package com.khan366kos.common.models.business

import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId
import com.khan366kos.common.models.simple.WriteAccess
import kotlinx.serialization.Serializable

@Serializable
data class Property(
    val type: Int,
    val contract: Identifier,
    val definition: Identifier,
    val linkedPropertyInfo: LinkedPropertyInfo,
    val value: Identifier? = null,
    val isOwn: Boolean,
    val isLinked: Boolean,
    val contractPropertySource: Identifier,
    val evaluationPropertyInfo: EvaluationPropertyInfo,
    val writeAccess: WriteAccess,
    val name: String,
    val objectId: ObjectId,
    val typeId: TypeId
)