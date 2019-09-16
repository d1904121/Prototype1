package com.example.prototype1.models

import com.f3401pal.checkabletreeview.Node

class RawTreeNode(
    var value:Node,
    var children:MutableList<RawTreeNode>,
    var parent: RawTreeNode?=null,
    progress:Int=0
){
    constructor(value: Node):this(value, mutableListOf<RawTreeNode>())
    constructor():this(Node("")){
        //TODO:load from realm
    }
    var progress:Int = 0
        set(value) {
        //TODO
        field=value
    }

    fun save(){

    }

}