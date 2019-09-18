package com.example.prototype1.models

import com.google.gson.annotations.Expose
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmModule
import java.util.*

@RealmModule(allClasses = true)
open class RawTreeNode(
    @Expose open var value: Node?=null,
    @Expose open var children:RealmList<RawTreeNode>,
    @Expose open var parent: RawTreeNode?=null,
    @Expose open var progress:Int=0,
    @Expose @PrimaryKey open var uuid:String=UUID.randomUUID().toString()
):RealmObject(){
    constructor():this(children=RealmList<RawTreeNode>())
    constructor(value:Node):this(value,children=RealmList<RawTreeNode>())
    constructor(value:Node,parent:RawTreeNode?):this(value,children=RealmList<RawTreeNode>(),parent = parent)
    constructor(seedRoot:TreeSeedNode,parent: RawTreeNode?=null):this(){
        this.value=seedRoot.value
        this.progress=0
        this.parent=parent
        this.uuid=UUID.randomUUID().toString()
        this.children.clear()
        seedRoot.children.forEach {
            this.children.add(RawTreeNode(it,this))
        }
    }

    fun getRoot(): RawTreeNode {
        var result=this
        while(result.parent!=null)result= result.parent!!
        return result
    }

}