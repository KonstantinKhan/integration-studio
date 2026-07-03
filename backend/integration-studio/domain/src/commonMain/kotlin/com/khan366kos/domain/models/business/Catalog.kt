package com.khan366kos.domain.models.business

import com.khan366kos.domain.models.simple.*

data class Catalog(
    val name: ElementName,
    val objectId: ObjectId,
    val typeId: TypeId,
)