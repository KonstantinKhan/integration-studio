package com.khan366kos.domain.classifier

data class ClassifierGroup(
    val minValue: Long,
    val maxValue: Long,
    val level: Int,
    val name: String,
) {
    fun contains(other: ClassifierGroup): Boolean =
        this.minValue <= other.minValue && this.maxValue >= other.maxValue
}
