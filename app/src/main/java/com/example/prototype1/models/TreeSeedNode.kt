package com.example.prototype1.models

import com.f3401pal.checkabletreeview.Node

class TreeSeedNode(
    var value: Node,
    var children:MutableList<TreeSeedNode>,
    var parent: TreeSeedNode?
) {
    constructor():this(Node(""), mutableListOf<TreeSeedNode>(),null)
    constructor(json:String) :this()  {
        //TODO
        val n=Node("")
        value=n
    }
    constructor(rawTreeNode: RawTreeNode):this(){
        //TODO
    }
    fun toJson():String{
        //TODO
        return ""
    }
    fun generateRawTreeNode():RawTreeNode{
        //TODO
        return RawTreeNode(Node(""))
    }
}