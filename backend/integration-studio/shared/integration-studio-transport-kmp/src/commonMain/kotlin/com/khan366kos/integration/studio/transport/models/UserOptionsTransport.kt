package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserOptionsTransport(
    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("viewApplicableOnly")
    val viewApplicableOnly: Boolean = false,

    @SerialName("objectsAutoSave")
    val objectsAutoSave: Boolean = false,

    @SerialName("favoritesCatalog")
    val favoritesCatalog: IdentifiableObjectTransport? = null,

    @SerialName("storedConditions")
    val storedConditions: List<IdentifiableObjectTransport>? = null,

    @SerialName("additionalUserOptions")
    val additionalUserOptions: AdditionalUserOptionsTransport? = null
)