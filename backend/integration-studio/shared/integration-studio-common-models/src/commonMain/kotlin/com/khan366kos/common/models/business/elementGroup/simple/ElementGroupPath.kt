package com.khan366kos.common.models.business.elementGroup.simple

import com.khan366kos.common.models.simple.NamePath
import com.khan366kos.common.models.simple.ObjectId
import com.khan366kos.common.models.simple.TypeId
import kotlinx.serialization.Serializable


@Serializable
data class ElementGroupPath (
    val name: NamePath,
    val objectId: ObjectId,
    val typeId: TypeId
){
}