package com.example.prototype1.utils

import com.example.prototype1.checkabletreeview.models.NodeDetailMap
import com.example.prototype1.checkabletreeview.models.ViewTreeNode
import com.example.prototype1.checkabletreeview.views.SingleRecyclerViewImpl
import com.example.prototype1.checkabletreeview.views.TreeAdapter
import com.example.prototype1.models.Node
import com.example.prototype1.models.RawTreeNode
import io.realm.Realm

class NodeUtils {
    fun getRoot(realm:Realm):RawTreeNode{
        realm.beginTransaction()
        val result=realm.where(RawTreeNode::class.java).findFirst()
            ?:realm.createObject(RawTreeNode::class.java).apply {
                value=realm.createObject(Node::class.java).apply {
                    str="root"
                    detail=realm.createObject(NodeDetailMap::class.java)
                }
            }
        realm.commitTransaction()
        return result
    }

    fun refreshView(view:SingleRecyclerViewImpl,root:RawTreeNode){
        view.setRoots(mutableListOf(ViewTreeNode(root,null,(view.adapter as TreeAdapter).viewNodes.firstOrNull())))
    }
}