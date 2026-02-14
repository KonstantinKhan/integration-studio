package com.khan366kos.common.responses

import com.khan366kos.common.models.contracts.Contract
import com.khan366kos.common.models.definitions.Definitions
import com.khan366kos.common.models.items.Item
import com.khan366kos.common.models.measure.MeasureEntities
import com.khan366kos.common.models.measure.MeasureUnits
import com.khan366kos.common.models.values.Values
import kotlinx.serialization.Serializable

@Serializable
data class PropertyOwnerRespose(
    val contracts: List<Contract>,
    val definitions: Definitions,
    val measureEntities: MeasureEntities,
    val measureUnits: MeasureUnits,
    val values: Values,
    val items: List<Item>
)