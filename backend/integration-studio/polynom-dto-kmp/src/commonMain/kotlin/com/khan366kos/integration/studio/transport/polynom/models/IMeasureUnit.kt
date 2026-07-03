package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IMeasureUnit(
    @SerialName("name")
    val name: String? = null,

    @SerialName("code")
    val code: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("uid")
    val uid: String,

    @SerialName("isBasic")
    val isBasic: Boolean,

    @SerialName("designation")
    val designation: String? = null,

    @SerialName("fromBasicFactor")
    val fromBasicFactor: Double,

    @SerialName("fromBasicOffset")
    val fromBasicOffset: Double,

    @SerialName("measureEntity")
    val measureEntity: IIdentifiableObject,

    @SerialName("codeOkei")
    val codeOkei: String? = null,

    @SerialName("internationalDesignationOkei")
    val internationalDesignationOkei: String? = null,

    @SerialName("internationalLiteralDesignation")
    val internationalLiteralDesignation: String? = null,

    @SerialName("literalDesignation")
    val literalDesignation: String? = null,

)
