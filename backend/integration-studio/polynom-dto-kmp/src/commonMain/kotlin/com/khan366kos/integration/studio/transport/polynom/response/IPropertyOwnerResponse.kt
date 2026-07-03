package com.khan366kos.integration.studio.transport.polynom.response

import com.khan366kos.integration.studio.transport.polynom.models.IAbleMeasureEntities
import com.khan366kos.integration.studio.transport.polynom.models.IAbleMeasureUnits
import com.khan366kos.integration.studio.transport.polynom.models.IAblePropertyDefinitions
import com.khan366kos.integration.studio.transport.polynom.models.IAblePropertyValues
import com.khan366kos.integration.studio.transport.polynom.models.IContractRef
import com.khan366kos.integration.studio.transport.polynom.models.IPropertyOwnerRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IPropertyOwnerResponse(
    @SerialName("measureEntities")
    val measureEntities: IAbleMeasureEntities,

    @SerialName("measureUnits")
    val measureUnits: IAbleMeasureUnits,

    @SerialName("definitions")
    val definitions: IAblePropertyDefinitions,

    @SerialName("values")
    val values: IAblePropertyValues,

    @SerialName("allContracts")
    val allContracts: List<IContractRef>? = null,

    @SerialName("propertyOwner")
    val propertyOwner: IPropertyOwnerRef,
)