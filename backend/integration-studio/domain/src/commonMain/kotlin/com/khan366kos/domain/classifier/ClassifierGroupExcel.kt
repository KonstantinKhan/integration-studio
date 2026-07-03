package com.khan366kos.domain.classifier

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId
import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.domain.polynom.models.Level
import com.khan366kos.domain.polynom.models.MaxValue
import com.khan366kos.domain.polynom.models.MinValue

data class ClassifierGroupExcel(
    val minValue: MinValue = MinValue.NONE,
    val maxValue: MaxValue = MaxValue.NONE,
    val level: Level = Level.NONE,
    val name: ElementName,
) {
    fun contains(other: ClassifierGroupExcel): Boolean =
        this.minValue.value <= other.minValue.value && this.maxValue.value >= other.maxValue.value

    companion object {
        val NONE = ClassifierGroupExcel(name = ElementName.NONE)
    }
}

fun ClassifierGroupExcel.toReference() = ClassifierTreeNode.Reference(
    minValue = minValue,
    maxValue = maxValue,
    objectId = ObjectId.NONE,
    typeId = TypeId.NONE,
    level = level,
    name = name
)

fun ClassifierGroupExcel.toCatalog() = ClassifierTreeNode.Catalog(
    minValue = minValue,
    maxValue = maxValue,
    objectId = ObjectId.NONE,
    typeId = TypeId.NONE,
    level = level,
    name = name,
)

fun ClassifierGroupExcel.toGroup() = ClassifierTreeNode.Group(
    minValue = minValue,
    maxValue = maxValue,
    objectId = ObjectId.NONE,
    typeId = TypeId.NONE,
    level = level,
    name = name
)
