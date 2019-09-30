package com.example.prototype1.treeview.utils
import android.os.SystemClock
import com.example.prototype1.treeview.models.ViewTreeNode
import com.example.prototype1.models.NodeValue

object IdGenerator {

    private val base by lazy {
        SystemClock.currentThreadTimeMillis()
    }
    private var count = 0

    fun generate(): Long {
        return base + count--
    }
}

object TreeNodeFactory {

    fun buildTestTree(): ViewTreeNode {
        val root =
            ViewTreeNode(NodeValue("root"))
        val left = ViewTreeNode(
            NodeValue("left"),
            root
        ).apply {
            children=mutableListOf(
                ViewTreeNode(
                    NodeValue("level3left"),
                    this
                ),
                ViewTreeNode(
                    NodeValue("level3right"),
                    this
                )
            )
        }
        val right = ViewTreeNode(
            NodeValue("right"),
            root
        )

        root.children = mutableListOf(left, right)
        return root
    }
}
