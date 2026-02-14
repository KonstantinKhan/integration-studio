package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdditionalUserOptionsTransport(
    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("userOptionsForRestrictedLists")
    val userOptionsForRestrictedLists: List<UserOptionsForRestrictedListTransport>? = null,

    @SerialName("isSuitableForAllRestrictedLists")
    val isSuitableForAllRestrictedLists: Boolean = false,

    @SerialName("isWithoutAllRestrictedLists")
    val isWithoutAllRestrictedLists: Boolean = false
)