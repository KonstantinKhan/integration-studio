package com.khan366kos.domain.polynom.models

import com.khan366kos.domain.models.simple.ElementName
import com.khan366kos.domain.models.simple.ObjectId
import com.khan366kos.domain.models.simple.TypeId

sealed class ClassifierTreeNode(
    open val minValue: MinValue,
    open val maxValue: MaxValue,
    open val objectId: ObjectId,
    open val typeId: TypeId,
    open val level: Level,
    open val name: ElementName
) {

    data object Empty : ClassifierTreeNode(
        minValue = MinValue.NONE,
        maxValue = MaxValue.NONE,
        objectId = ObjectId.NONE,
        typeId = TypeId.NONE,
        level = Level.NONE,
        name = ElementName.NONE
    )

    data class Reference(
        override val minValue: MinValue = MinValue.NONE,
        override val maxValue: MaxValue = MaxValue.NONE,
        override val objectId: ObjectId,
        override val typeId: TypeId,
        override val level: Level = Level.NONE,
        override val name: ElementName,
        val catalogs: MutableList<Catalog> = mutableListOf()
    ) : ClassifierTreeNode(minValue, maxValue, objectId, typeId, level, name) {

        companion object {
            val NONE = Reference(
                minValue = MinValue.NONE,
                maxValue = MaxValue.NONE,
                objectId = ObjectId.NONE,
                typeId = TypeId.NONE,
                name = ElementName.NONE,
                level = Level.NONE,
            )
        }
    }

    data class Catalog(
        override val minValue: MinValue = MinValue.NONE,
        override val maxValue: MaxValue = MaxValue.NONE,
        override val objectId: ObjectId,
        override val typeId: TypeId,
        override val level: Level = Level.NONE,
        override val name: ElementName,
        val groups: MutableList<Group> = mutableListOf()
    ) : ClassifierTreeNode(minValue, maxValue, objectId, typeId, level, name) {

        companion object {
            val NONE = Catalog(
                minValue = MinValue.NONE,
                maxValue = MaxValue.NONE,
                objectId = ObjectId.NONE,
                typeId = TypeId.NONE,
                name = ElementName.NONE,
                level = Level.NONE,
            )
        }
    }

    data class Group(
        override val minValue: MinValue,
        override val maxValue: MaxValue,
        override val objectId: ObjectId,
        override val typeId: TypeId,
        override val level: Level,
        override val name: ElementName,
        val groups: MutableList<Group> = mutableListOf()
    ) : ClassifierTreeNode(minValue, maxValue, objectId, typeId, level, name) {

        companion object {
            val NONE = Group(
                minValue = MinValue.NONE,
                maxValue = MaxValue.NONE,
                objectId = ObjectId.NONE,
                typeId = TypeId.NONE,
                name = ElementName.NONE,
                level = Level.NONE,
            )
        }
    }
}