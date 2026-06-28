package com.khan366kos.domain.models.business.elementGroup.simple

import com.khan366kos.domain.models.simple.NamePath
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import kotlinx.serialization.Serializable


@Serializable
data class ElementGroupPath (
    val name: NamePath,
    val objectId: ObjectId,
    val typeId: TypeId
){
}