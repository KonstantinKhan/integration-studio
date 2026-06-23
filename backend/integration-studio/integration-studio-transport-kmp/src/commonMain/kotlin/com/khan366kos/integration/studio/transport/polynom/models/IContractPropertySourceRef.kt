package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IContractPropertySourceRef(
    @SerialName("name")
    val name: String? = null,

    @SerialName("id")
    val id: String? = null,

    @SerialName("absoluteCode")
    val absoluteCode: String? = null,

    @SerialName("writeAccess")
    val writeAccess: Boolean,

    @SerialName("objectId")
    val objectId: Int,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("isMandatory")
    val isMandatory: Boolean,

    @SerialName("isMandatoryEnabled")
    val isMandatoryEnabled: Boolean,

    @SerialName("isHidden")
    val isHidden: Boolean,

    @SerialName("isHiddenEnabled")
    val isHiddenEnabled: Boolean,

    @SerialName("isSpecial")
    val isSpecial: Boolean,

    @SerialName("isSpecialEnabled")
    val isSpecialEnabled: Boolean,

    @SerialName("isUnique")
    val isUnique: Boolean,

    @SerialName("isUniqueEnabled")
    val isUniqueEnabled: Boolean,

    @SerialName("isIndexable")
    val isIndexable: Boolean,

    @SerialName("isIndexableEnabled")
    val isIndexableEnabled: Boolean,

    @SerialName("isDynamic")
    val isDynamic: Boolean,

    @SerialName("isDynamicEnabled")
    val isDynamicEnabled: Boolean,

    @SerialName("isDisplayedForSelection")
    val isDisplayedForSelection: Boolean,

    @SerialName("isDisplayedForSelectionEnabled")
    val isDisplayedForSelectionEnabled: Boolean,

    @SerialName("isSetBeforeApplying")
    val isSetBeforeApplying: Boolean,

    @SerialName("isSetBeforeApplyingEnabled")
    val isSetBeforeApplyingEnabled: Boolean,

    @SerialName("isUsedInModelFamiliesDefault")
    val isUsedInModelFamiliesDefault: Boolean,

    @SerialName("isUsedInModelFamiliesDefaultEnabled")
    val isUsedInModelFamiliesDefaultEnabled: Boolean,

    @SerialName("isReadOnly")
    val isReadOnly: Boolean,

    @SerialName("isReadOnlyEnabled")
    val isReadOnlyEnabled: Boolean,

    @SerialName("position")
    val position: Int,

    @SerialName("isDefaultIfEmpty")
    val isDefaultIfEmpty: Boolean,

    @SerialName("isDefaultIfEmptyEnabled")
    val isDefaultIfEmptyEnabled: Boolean,

    @SerialName("isDefaultEnabled")
    val isDefaultEnabled: Boolean,

    @SerialName("isNameManuallySet")
    val isNameManuallySet: Boolean,

    @SerialName("propertySource")
    val propertySource: IPropertySourceRef,

    @SerialName("defaultMeasureUnit")
    val defaultMeasureUnit: IIdentifiableObject,

    @SerialName("defaultPropertyValue")
    val defaultPropertyValue: IIdentifiableObject,

    @SerialName("appointedFormula")
    val appointedFormula: INamedObject,

    @SerialName("ownerContract")
    val ownerContract: INamedObject
)
