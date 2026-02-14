package com.khan366kos.common.models.measure

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class MeasureUnits(
    val units: List<MeasureUnit>
)

@Serializable
data class MeasureUnit(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: String,
    val uid: String, // Using string representation of UUID
    val code: String,
    val isBasic: Boolean,
    val designation: String,
    val fromBasicFactor: Double,
    val fromBasicOffset: Double,
    val measureEntity: Identifier,
    val codeOkei: String,
    val internationalDesignationOkei: String,
    val internationalLiteralDesignation: String,
    val literalDesignation: String
)