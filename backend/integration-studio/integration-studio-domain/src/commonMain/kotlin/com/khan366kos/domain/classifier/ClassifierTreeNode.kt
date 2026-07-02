package com.khan366kos.domain.classifier

data class ClassifierTreeNode(
    val group: ClassifierGroup,
    val children: MutableList<ClassifierTreeNode> = mutableListOf()
)
