package com.khan366kos.integration.studio.transport.polynom.models.concept

import com.khan366kos.integration.studio.transport.polynom.models.IIdentifiableObject
import com.khan366kos.integration.studio.transport.polynom.models.INamedObject
import kotlinx.serialization.Serializable

@Serializable
data class IConceptPropertySource(
    val name: String? = null,
    val id: String? = null,
    val absoluteCode: String? = null,
    val writeAccess: Boolean,
    val objectId: Int,
    val typeId: Int,
    val contract: INamedObject? = null,
    val type: Int,
    val isLinked: Boolean,
    val isMandatory: Boolean,
    val isMandatoryEnabled: Boolean,
    val isHidden: Boolean,
    val isHiddenEnabled: Boolean,
    val isSpecial: Boolean,
    val isSpecialEnabled: Boolean,
    val isUnique: Boolean,
    val isUniqueEnabled: Boolean,
    val isIndexable: Boolean,
    val isIndexableEnabled: Boolean,
    val isDynamic: Boolean,
    val isDynamicEnabled: Boolean,
    val isDisplayedForSelection: Boolean,
    val isDisplayedForSelectionEnabled: Boolean,
    val isSetBeforeApplying: Boolean,
    val isSetBeforeApplyingEnabled: Boolean,
    val isUsedInModelFamiliesDefault: Boolean,
    val isUsedInModelFamiliesDefaultEnabled: Boolean,
    val isReadOnly: Boolean,
    val isReadOnlyEnabled: Boolean,
    val position: Int,
    val isDefaultIfEmpty: Boolean,
    val isDefaultIfEmptyEnabled: Boolean,
    val isDefaultEnabled: Boolean,
    val isNameManuallySet: Boolean,
    val propertySource: IPropertySource,
    val measureEntity: IIdentifiableObject? = null,
    val defaultMeasureUnit: IIdentifiableObject,
    val defaultPropertyValue: IIdentifiableObject? = null,
    val isInherited: Boolean,
)
