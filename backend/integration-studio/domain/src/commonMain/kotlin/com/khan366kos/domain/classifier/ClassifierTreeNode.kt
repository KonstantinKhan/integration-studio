package com.khan366kos.domain.classifier

data class ClassifierTreeNode(
    val group: ClassifierGroupExcel,
    val children: MutableList<ClassifierTreeNode> = mutableListOf()
) {
    fun root() = this
}
