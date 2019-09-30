package com.example.prototype1.treeview.models

import com.example.prototype1.treeview.utils.IdGenerator
import com.example.prototype1.treeview.views.Expandable
import com.example.prototype1.treeview.views.HasId
import com.example.prototype1.treeview.views.NodeCheckedStatus
import com.example.prototype1.models.NodeValue
import com.example.prototype1.models.RawTreeNode
import com.example.prototype1.utils.AppUtils
import com.google.gson.annotations.Expose
import io.realm.Realm

class ViewTreeNode(
    @Expose var value: NodeValue,
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
    constructor(value: NodeValue) : this(value,ViewNodeTypes.NODE, null,  mutableListOf<ViewTreeNode>())
    // constructor for leaf node
    constructor(value: NodeValue, parent: ViewTreeNode) : this(value,ViewNodeTypes.NODE, parent,  mutableListOf<ViewTreeNode>())
    // constructor for parent node
    constructor(value: NodeValue, children: MutableList<ViewTreeNode>) : this(value,ViewNodeTypes.NODE, null, children)

    constructor(raw:RawTreeNode,parent:ViewTreeNode?=null,
                before:ViewTreeNode?=null):this(NodeValue()){
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
        this.children.add(ViewTreeNode(NodeValue(checked = true),ViewNodeTypes.QUICK_CREATE_NODE,this, mutableListOf()))
    }

    fun isTop(): Boolean {
        return parent == null
    }
    fun isLeaf(): Boolean {
        return children.isEmpty()
//        return children.size<=1//1: quick create node
    }

    fun getLevel(): Int {
        fun stepUp (viewNode: ViewTreeNode): Int {
            return viewNode.parent?.let { 1 + stepUp(it) } ?: 0
        }
        return stepUp(this)
    }
    fun setChecked(isChecked: Boolean,realm: Realm?,viewNode:ViewTreeNode) {
        if (realm != null) {
            AppUtils().executeTransactionIfNotInTransaction(realm){
                viewNode.value.checked=rawReference?.progress?:0>0
                viewNode.children.forEach {
                    it.rawReference?.progress=if(rawReference?.progress?:0>0)1 else 0
                    setChecked(isChecked,realm,it)
                }
            }
        }
    }
    fun getCheckedStatus(): NodeCheckedStatus {
        if(type==ViewNodeTypes.QUICK_CREATE_NODE) {
            return NodeCheckedStatus(false, true)
        }
        //1:quick create
        if (children.size<=1) return NodeCheckedStatus(value.checked, value.checked)
        var hasChildChecked = false
        var allChildrenChecked = true
        children.forEach {
            val checkedStatus = it.getCheckedStatus()
            hasChildChecked = hasChildChecked || checkedStatus.hasChildChecked
            allChildrenChecked = allChildrenChecked && checkedStatus.allChildrenChecked
        }
        return NodeCheckedStatus(hasChildChecked, allChildrenChecked)
    }
    fun getAggregatedValues(): List<NodeValue> {
        return if (isLeaf()) {
            if (value.checked) listOf(value) else emptyList()
        } else {
            if (getCheckedStatus().allChildrenChecked) {
                listOf(value)
            } else {
                val result = mutableListOf<NodeValue>()
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