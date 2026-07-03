package com.khan366kos.integration.studio.transport.polynom.models.concept

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.Serializable

@Serializable
data class IPropertySource(
    val id: String? = null,
    val absoluteCode: String? = null,
    val writeAccess: Boolean,
    val objectId: Int,
    val typeId: Int,
    val definition: IPropertyDefinitionWithItems,
    val ownerContract: IIdentifiableObject,
    val isLinked: Boolean,
    val linkDefinitionEnd: INamedObject,
    val linkedConceptPropertySource: IIdentifiableObject
)
