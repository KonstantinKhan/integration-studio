package com.khan366kos.common.models.items

import com.khan366kos.common.models.business.Identifier
import com.khan366kos.common.models.contracts.Contract
import com.khan366kos.common.models.simple.*
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val objectId: ObjectId,
    val typeId: TypeId,
    val id: String,
    val selfContracts: List<Contract>,
    val properties: List<ItemProperty>
)

@Serializable
data class ItemProperty(
    val objectId: ObjectId,
    val typeId: TypeId,
    val dataType: Int,
    val contract: Identifier,
    val contractProperty: Identifier,
    val definition: Identifier,
    val valueData: Identifier?,
    val evaluationMode: Int,
    val isLinked: Boolean
)

@Serializable
data class ItemsList(
    val items: List<Item>
)