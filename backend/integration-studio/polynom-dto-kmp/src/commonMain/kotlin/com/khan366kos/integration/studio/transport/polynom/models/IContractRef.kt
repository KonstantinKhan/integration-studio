package com.khan366kos.integration.studio.transport.polynom.models

import com.khan366kos.integration.studio.transport.polynom.models.properties.values.ITableValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IContractRef(
    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("absoluteCode")
    val absoluteCode: String? = null,

    @SerialName("code")
    val code: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("isSystemObject")
    val isSystemObject: Boolean,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("properties")
    val properties: List<IContractPropertySourceRef>,

    @SerialName("canUnassign")
    val canUnassign: Boolean,

    @SerialName("superConcept")
    val superConcept: INamedObject? = null,

    @SerialName("ownPropertyValues")
    val ownPropertyValues: Boolean

    )
