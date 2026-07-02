package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.classifier.ClassifierGroup
import com.khan366kos.domain.classifier.ClassifierTreeNode

class ClassifierTreeBuilder {
    fun build(groups: List<ClassifierGroup>): ClassifierTreeNode {
        val roots = mutableListOf<ClassifierTreeNode>()
        val stack = ArrayDeque<ClassifierTreeNode>()

        for (group in groups) {
            val node = ClassifierTreeNode(group)
            while (stack.isNotEmpty() && stack.last().group.level >= group.level) {
                stack.removeLast()
            }
            if (stack.isEmpty()) roots.add(node) else stack.last().children.add(node)
            stack.addLast(node)
        }
        return roots.first()
    }

    fun toPreOrder(groups: List<ClassifierGroup>): List<ClassifierGroup> =
        groups.sortedWith(compareBy({ it.minValue }, { -it.maxValue }))

    fun printTree(nodes: List<ClassifierTreeNode>, prefix: String = "") {
        nodes.forEachIndexed { index, node ->
            val isLast = index == nodes.lastIndex
            val connector = if (isLast) "└── " else "├── "
            val childPrefix = if (isLast) "    " else "│   "
            println("$prefix$connector${node.group.name} (L${node.group.level})")
            if (node.children.isNotEmpty()) {
                printTree(node.children, "$prefix$childPrefix")
            }
        }
    }
}