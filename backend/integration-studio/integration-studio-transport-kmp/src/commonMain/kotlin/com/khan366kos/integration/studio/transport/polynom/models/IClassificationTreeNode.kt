package com.khan366kos.integration.studio.transport.polynom.models

import kotlinx.serialization.Serializable

@Serializable
data class IClassificationTreeNode(
    val name: String? = null,
    val icon: Int? = null,
    val iconColor: Int? = null,
    val isSystemObject: Boolean,
    val writeAccess: Boolean,
    val applicability: Int,
    val objectId: Int,
    val typeId: Int,
    val leaf: Boolean,
    val count: Int,
    val parent: IAccessControlObject? = null,
    val nodeObject: IAccessControlObject,
    val filterConditionApply: Boolean,
    val accessRight: Int,
    val hasObjects: Boolean
)
