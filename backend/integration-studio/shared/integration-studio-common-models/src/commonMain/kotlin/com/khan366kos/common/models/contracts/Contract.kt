package com.khan366kos.common.models.contracts

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.business.PropertySource
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Contract(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: String,
    val description: String? = null,
    val properties: List<ContractProperty>,
    val canUnassign: Boolean,
    val id: String,
    val absoluteCode: String,
    val code: String,
    val isSystemObject: Boolean,
    val writeAccess: WriteAccess,
)

@Serializable
data class ContractProperty(
    val id: String,
    val objectId: ObjectId,
    val typeId: TypeId,
    val dataType: Int? = null,
    val definition: Identifier? = null,
    val defaultValue: Identifier? = null,
    val absoluteCode: String,
    val isMandatory: Boolean,
    val isHidden: Boolean,
    val isSpecial: Boolean,
    val isUnique: Boolean,
    val isIndexable: Boolean,
    val isDynamic: Boolean,
    val isDisplayedForSelection: Boolean,
    val isSetBeforeApplying: Boolean,
    val isReadOnly: Boolean,
    val isDefaultIfEmpty: Boolean,
    val isNameManuallySet: Boolean,
    val position: Int,
    val isMandatoryEnabled: Boolean,
    val isHiddenEnabled: Boolean,
    val isSpecialEnabled: Boolean,
    val isUniqueEnabled: Boolean,
    val isIndexableEnabled: Boolean,
    val isDynamicEnabled: Boolean,
    val isDisplayedForSelectionEnabled: Boolean,
    val isSetBeforeApplyingEnabled: Boolean,
    val isUsedInModelFamiliesDefault: Boolean,
    val isUsedInModelFamiliesDefaultEnabled: Boolean,
    val isReadOnlyEnabled: Boolean,
    val isDefaultIfEmptyEnabled: Boolean,
    val isDefaultEnabled: Boolean,
    val propertySource: PropertySource,
    val writeAccess: WriteAccess,
    val name: String,
    val defaultPropertyValue: Identifier? = null,
    val defaultMeasureUnit: Identifier? = null,
)