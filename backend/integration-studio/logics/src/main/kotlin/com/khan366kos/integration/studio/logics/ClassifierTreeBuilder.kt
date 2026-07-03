package com.khan366kos.integration.studio.logics

import com.khan366kos.domain.classifier.ClassifierGroupExcel
import com.khan366kos.domain.classifier.toCatalog
import com.khan366kos.domain.classifier.toGroup
import com.khan366kos.domain.classifier.toReference
import com.khan366kos.domain.polynom.models.ClassifierTreeNode
import com.khan366kos.domain.polynom.models.ClassifierTreeNode.Reference

class ClassifierTreeBuilder {
    fun build(excelGroups: List<ClassifierGroupExcel>): ClassifierTreeNode {
        var reference: Reference = Reference.NONE
        val stack = ArrayDeque<ClassifierTreeNode>()

        for (excelGroup in excelGroups) {
            val node = when {
                excelGroup.level.asInt() == 0 -> excelGroup.toReference()
                excelGroup.level.asInt() == 1 -> excelGroup.toCatalog()
                excelGroup.level.asInt() >= 2 -> excelGroup.toGroup()
                else -> {
                    println("excel group $excelGroup")
                    ClassifierTreeNode.Empty
                }
            }

            while (stack.isNotEmpty() && stack.last().level.asInt() >= excelGroup.level.asInt()) {
                stack.removeLast()
            }

            when (node) {
                is Reference -> if (stack.isEmpty()) {
                    reference = node
                    stack.addLast(node)
                }

                is ClassifierTreeNode.Catalog ->
                    if (stack.last() is Reference && stack.isNotEmpty()) {
                        (stack.last() as Reference).catalogs.add(node)
                        stack.addLast(node)
                    }

                is ClassifierTreeNode.Group -> {
                    if (stack.isNotEmpty()) {
                        when (val element = stack.last()) {
                            is ClassifierTreeNode.Catalog -> element.groups.add(node)
                            is ClassifierTreeNode.Group -> element.groups.add(node)
                            else -> throw IllegalStateException("Unexpected element $element")
                        }
                    }
                    stack.addLast(node)
                }

                else -> throw IllegalStateException("Unexpected classifier tree node")
            }
        }
        return reference
    }

    fun toPreOrder(groups: List<ClassifierGroupExcel>): List<ClassifierGroupExcel> =
        groups.sortedWith(compareBy({ it.minValue.asLong() }, { -it.maxValue.asLong() }))

    fun printTree(nodes: List<ClassifierTreeNode>, prefix: String = "") {
        nodes.forEachIndexed { index, node ->
            val isLast = index == nodes.lastIndex
            val connector = if (isLast) "└── " else "├── "
            val childPrefix = if (isLast) "    " else "│   "
            println("$prefix$connector${node.name} (L${node.level.asInt()})")
            when (node) {
                is Reference -> if (node.catalogs.isNotEmpty()) printTree(node.catalogs, "$prefix$childPrefix")
                is ClassifierTreeNode.Catalog -> if (node.groups.isNotEmpty()) printTree(
                    node.groups,
                    "$prefix$childPrefix"
                )

                is ClassifierTreeNode.Group -> if (node.groups.isNotEmpty()) printTree(
                    node.groups,
                    "$prefix$childPrefix"
                )

                else -> throw IllegalStateException("Unexpected classifier tree node")
            }
        }
    }
}