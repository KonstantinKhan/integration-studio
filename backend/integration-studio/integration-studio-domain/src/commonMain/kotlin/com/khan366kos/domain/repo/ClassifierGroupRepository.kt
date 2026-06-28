package com.khan366kos.domain.repo

import com.khan366kos.domain.models.classifier.ClassifierGroup
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface ClassifierGroupRepository {
    fun save(classifierGroup: ClassifierGroup)
    fun saveAll(classifierGroups: List<ClassifierGroup>)
    fun findById(id: Uuid): ClassifierGroup?
    fun findAll(): List<ClassifierGroup>
    fun update(classifierGroup: ClassifierGroup)
    fun delete(id: Uuid)
}