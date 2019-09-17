package com.example.prototype1.models

import java.util.*

class TreeSeedNode(
    var value: Node,
    var children:MutableList<TreeSeedNode>,
    var parent: TreeSeedNode?,
    var uuid:String= UUID.randomUUID().toString()
) {
    constructor():this(Node(""), mutableListOf<TreeSeedNode>(),null)
    constructor(json:String) :this()  {
        //TODO:generate from json
        val n= Node("")
        value=n
    }
    constructor(rawTreeNode: RawTreeNode):this(){
        //TODO:generate from rawTreeNode
    }
    fun toJson():String{
        //TODO
        return ""
    }
}