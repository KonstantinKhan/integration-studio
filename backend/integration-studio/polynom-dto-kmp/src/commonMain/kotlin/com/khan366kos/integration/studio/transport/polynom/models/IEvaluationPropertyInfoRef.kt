package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IEvaluationPropertyInfoRef(
    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("evaluationMode")
    val evaluationMode: Int,

    @SerialName("formula")
    val formula: INamedObject,

    @SerialName("appointedFormula")
    val appointedFormula: INamedObject,

    @SerialName("usePropertyValue")
    val usePropertyValue: Boolean,

    @SerialName("evaluationErrorMessage")
    val evaluationErrorMessage: String? = null
)
