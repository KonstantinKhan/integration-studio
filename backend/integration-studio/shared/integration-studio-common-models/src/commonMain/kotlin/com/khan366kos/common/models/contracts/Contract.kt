package com.khan366kos.common.models.contracts

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Contract(
    val objectId: ObjectId,
    val typeId: TypeId,
    val name: String,
    val description: String?,
    val properties: List<ContractProperty>
)

@Serializable
data class ContractProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val dataType: Int,
    val definition: Identifier,
    val defaultValue: Identifier?,
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
    val position: Int
)