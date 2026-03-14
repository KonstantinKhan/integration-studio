package com.khan366kos.common.models.classifier

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ClassifierGroup @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid,
    val min: Long,
    val max: Long,
    val level: Long,
    val name: String,
    val parendId: Uuid,
    val childsId: List<Uuid>
)
