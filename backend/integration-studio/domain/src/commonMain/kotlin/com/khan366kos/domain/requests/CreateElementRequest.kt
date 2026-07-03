package com.khan366kos.domain.requests

import com.khan366kos.domain.models.business.Identifier

data class CreateElementRequest(
    val parentGroup: Identifier,
    val name: String = ""
)
