package com.khan366kos.integration.studio.transport.polynom.models.concept

import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.Serializable

@Serializable
data class IAppointedConcept(
    val writeAccess: Boolean,
    val isSystemObject: Boolean,
    val objectId: Int,
    val typeId: Int,
    val position: Int,
    val isMandatory: Boolean? = null,
    val isInheritable: Boolean? = null,
    val isDisabled: Boolean? = null,
    val isPredefined: Boolean? = null,
    val isInherited: Boolean,
    val concept: INamedObject,
    val conceptAppointer: INamedObject
)
