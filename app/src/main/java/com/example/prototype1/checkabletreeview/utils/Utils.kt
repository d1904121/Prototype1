package com.example.prototype1.checkabletreeview.utils
import android.os.SystemClock
import com.example.prototype1.checkabletreeview.models.Node
import com.example.prototype1.checkabletreeview.models.ViewTreeNode

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
            ViewTreeNode(Node("root"))
        val left = ViewTreeNode(
            Node("left"),
            root
        ).apply {
            children=mutableListOf(
                ViewTreeNode(
                    Node("level3left"),
                    this
                ),
                ViewTreeNode(
                    Node("level3right"),
                    this
                )
            )
        }
        val right = ViewTreeNode(
            Node("right"),
            root
        )

        root.children = mutableListOf(left, right)
        return root
    }
}
