package com.khan366kos.integration.studio.transport.polynom.request

import kotlinx.serialization.Serializable

@Serializable
data class IClassificationTreeRequest(
    val pageNumber: Int,
    val pageSize: Int,
    val options: Int,
    val filterString: String? = null,
    val filterOptions: Int = 1,
) {
    companion object {
        val Root: IClassificationTreeRequest = IClassificationTreeRequest(
            pageNumber = 0,
            pageSize = 0,
            options = 0,
        )
    }
}
