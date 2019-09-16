package com.f3401pal.checkabletreeview

import com.google.gson.annotations.Expose

class ViewTreeNode<T : Checkable>(
    @Expose val value: T,
    val parent: ViewTreeNode<T>?,
    @Expose var children: MutableList<ViewTreeNode<T>>,
    @Expose override var isExpanded: Boolean =false
) : HasId, Expandable {
    override val id: Long by lazy {
        IdGenerator.generate()
    }
    // constructor for root node
    constructor(value: T) : this(value, null,  mutableListOf<ViewTreeNode<T>>())
    // constructor for leaf node
    constructor(value: T, parent: ViewTreeNode<T>) : this(value, parent,  mutableListOf<ViewTreeNode<T>>())
    // constructor for parent node
    constructor(value: T, children: MutableList<ViewTreeNode<T>>) : this(value, null, children)

    fun isTop(): Boolean {
        return parent == null
    }
    fun isLeaf(): Boolean {
        return children.isEmpty()
    }
    fun getLevel(): Int {
        fun stepUp (viewNode: ViewTreeNode<T>): Int {
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
    fun getAggregatedValues(): List<T> {
        return if (isLeaf()) {
            if (value.checked) listOf(value) else emptyList()
        } else {
            if (getCheckedStatus().allChildrenChecked) {
                listOf(value)
            } else {
                val result = mutableListOf<T>()
                children.forEach {
                    result.addAll(it.getAggregatedValues())
                }
                result
            }
        }
    }
    fun getRoot():ViewTreeNode<T>{
        var result=this
        while(result.parent!=null)result= result.parent!!
        return result
    }
}