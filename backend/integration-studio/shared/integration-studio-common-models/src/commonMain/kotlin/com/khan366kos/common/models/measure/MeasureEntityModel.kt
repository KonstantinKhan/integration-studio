package com.khan366kos.common.models.measure

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class MeasureEntities(
    val entities: List<MeasureEntity>
)

@Serializable
data class MeasureEntity(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: String,
    val uid: String, // Using string representation of UUID
    val code: String,
    val basicUnit: Identifier
)