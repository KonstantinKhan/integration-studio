package com.khan366kos.integration.studio.transport.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserOptionsForRestrictedListTransport(
    @SerialName("writeAccess")
    val writeAccess: Boolean = false,

    @SerialName("objectId")
    val objectId: Int = 0,

    @SerialName("typeId")
    val typeId: Int,

    @SerialName("isEnabled")
    val isEnabled: Boolean = false,

    @SerialName("restrictedList")
    val restrictedList: RestrictedListTransport? = null
)