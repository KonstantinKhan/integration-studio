package com.khan366kos.domain.models.simple

import kotlin.jvm.JvmInline

@JvmInline
value class ReferenceId(private val value: String) {
    companion object {
        val NONE = ReferenceId("")
    }
}
