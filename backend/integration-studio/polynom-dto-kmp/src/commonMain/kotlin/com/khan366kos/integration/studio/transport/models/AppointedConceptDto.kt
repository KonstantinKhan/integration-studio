package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointedConceptDto(
    @SerialName("writeAccess")
    val writeAccess: Boolean,
    @SerialName("isSystemObject")
    val isSystemObject: Boolean,
    @SerialName("objectId")
    val objectId: Int,
    @SerialName("typeId")
    val typeId: Int,
    @SerialName("position")
    val position: Int,
    @SerialName("isMandatory")
    val isMandatory: Boolean? = null,
    @SerialName("isInheritable")
    val isInheritable: Boolean? = null,
    @SerialName("isDisabled")
    val isDisabled: Boolean? = null,
    @SerialName("isPredefined")
    val isPredefined: Boolean? = null,
    @SerialName("isInherited")
    val isInherited: Boolean,
    @SerialName("concept")
    val concept: NamedObjectDto,
    @SerialName("conceptAppointer")
    val conceptAppointer: NamedObjectDto
)
