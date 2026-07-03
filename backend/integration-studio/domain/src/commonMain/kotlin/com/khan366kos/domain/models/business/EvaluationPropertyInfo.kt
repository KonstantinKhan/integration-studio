package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.models.simple.WriteAccess

data class EvaluationPropertyInfo(
    val evaluationMode: Int,
    val formula: Identifier,
    val appointedFormula: Identifier,
    val usePropertyValue: Boolean,
    val writeAccess: WriteAccess,
    val objectId: ObjectId,
    val typeId: TypeId,
)
