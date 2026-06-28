package com.khan366kos.integration.studio.bff.transport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PropertyValueBffDto {

    @Serializable
    @SerialName("string")
    data class StringValueBffDto(val data: String) : PropertyValueBffDto()

    @Serializable
    @SerialName("boolean")
    data class BooleanValueBffDto(val data: Boolean) : PropertyValueBffDto()
    @Serializable
    @SerialName("unknown")
    data class UnknownValueBffDto(val data: String) : PropertyValueBffDto()

    @Serializable
    @SerialName("dateTime")
    data class DateTimeValueBffDto(val data: String) : PropertyValueBffDto()

    @Serializable
    @SerialName("enum")
    data class EnumValueBffDto(val data: String) : PropertyValueBffDto()
}
