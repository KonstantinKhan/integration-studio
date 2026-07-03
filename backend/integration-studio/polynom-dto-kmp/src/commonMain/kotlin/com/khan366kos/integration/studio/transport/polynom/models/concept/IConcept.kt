package com.khan366kos.integration.studio.transport.polynom.models.concept

import com.khan366kos.integration.studio.transport.polynom.models.IAbleMeasureEntities
import com.khan366kos.integration.studio.transport.polynom.models.IAbleMeasureUnits
import com.khan366kos.integration.studio.transport.polynom.models.IAblePropertyDefinitions
import com.khan366kos.integration.studio.transport.polynom.models.IAblePropertyValues
import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import kotlinx.serialization.Serializable

@Serializable
data class IConcept(
    val name: String? = null,
    val id: String? = null,
    val absoluteCode: String? = null,
    val code: String? = null,
    val description: String? = null,
    val isSystemObject: Boolean,
    val writeAccess: Boolean,
    val objectId: Int,
    val typeId: Int,
    val superConcept: IIdentifiableObject,
    val conceptPropertySources: List<IConceptPropertySource>? = null,
    val definitions: IAblePropertyDefinitions,
    val values: IAblePropertyValues,
    val measureEntities: IAbleMeasureEntities,
    val measureUnits: IAbleMeasureUnits,
)
