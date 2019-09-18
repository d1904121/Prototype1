package com.example.prototype1.checkabletreeview.models

import com.example.prototype1.checkabletreeview.utils.IdGenerator
import com.example.prototype1.checkabletreeview.views.Expandable
import com.example.prototype1.checkabletreeview.views.HasId
import com.example.prototype1.checkabletreeview.views.NodeCheckedStatus
import com.example.prototype1.models.Node
import com.example.prototype1.models.RawTreeNode
import com.google.gson.annotations.Expose

class ViewTreeNode(
    @Expose var value: Node,
    var type:ViewNodeTypes=ViewNodeTypes.NODE,
    var parent: ViewTreeNode?,
    @Expose var children: MutableList<ViewTreeNode>,
    @Expose override var isExpanded: Boolean =false,
    var rawReference:RawTreeNode?=null
) : HasId, Expandable {
    override val id: Long by lazy {
        IdGenerator.generate()
    }
    // constructor for root node
    constructor(value: Node) : this(value,ViewNodeTypes.NODE, null,  mutableListOf<ViewTreeNode>())
    // constructor for leaf node
    constructor(value: Node, parent: ViewTreeNode) : this(value,ViewNodeTypes.NODE, parent,  mutableListOf<ViewTreeNode>())
    // constructor for parent node
    constructor(value: Node, children: MutableList<ViewTreeNode>) : this(value,ViewNodeTypes.NODE, null, children)

    constructor(raw:RawTreeNode,parent:ViewTreeNode?=null,
                before:ViewTreeNode?=null):this(Node()){
        this.parent=parent
        this.isExpanded=(before!=null && before.isExpanded)
        this.value=raw.value!!
        this.children.clear()
        //TODO: delete
        this.rawReference=raw
        raw.children.forEach {
            val childBefore= before?.children?.findLast {it2->
                it2.value.uuid== it.value?.uuid
            }
            this.children.add(ViewTreeNode(it,this,childBefore))
        }
        this.children.add(ViewTreeNode(Node(),ViewNodeTypes.QUICK_CREATE_NODE,this, mutableListOf()))
    }

    fun isTop(): Boolean {
        return parent == null
    }
    fun isLeaf(): Boolean {
        return children.isEmpty()
    }
    fun getLevel(): Int {
        fun stepUp (viewNode: ViewTreeNode): Int {
            return viewNode.parent?.let { 1 + stepUp(it) } ?: 0
        }
        return stepUp(this)
    }
    fun setChecked(isChecked: Boolean) {
        value.checked = isChecked
        // cascade the action to children
        children.forEach {
            it.setChecked(isChecked)
        }
    }
    fun getCheckedStatus(): NodeCheckedStatus {
        if (isLeaf()) return NodeCheckedStatus(value.checked, value.checked)
        var hasChildChecked = false
        var allChildrenChecked = true
        children.forEach {
            val checkedStatus = it.getCheckedStatus()
            hasChildChecked = hasChildChecked || checkedStatus.hasChildChecked
            allChildrenChecked = allChildrenChecked && checkedStatus.allChildrenChecked
        }
        return NodeCheckedStatus(hasChildChecked, allChildrenChecked)
    }
    fun getAggregatedValues(): List<Node> {
        return if (isLeaf()) {
            if (value.checked) listOf(value) else emptyList()
        } else {
            if (getCheckedStatus().allChildrenChecked) {
                listOf(value)
            } else {
                val result = mutableListOf<Node>()
                children.forEach {
                    result.addAll(it.getAggregatedValues())
                }
                result
            }
        }
    }
    fun getRoot(): ViewTreeNode {
        var result=this
        while(result.parent!=null)result= result.parent!!
        return result
    }
}