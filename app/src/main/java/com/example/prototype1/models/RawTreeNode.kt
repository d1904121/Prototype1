package com.example.prototype1.models

import com.example.prototype1.checkabletreeview.models.Node
import com.example.prototype1.checkabletreeview.models.ViewTreeNode
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmModule
import java.util.*

@RealmModule(allClasses = true)
open class RawTreeNode(
    @Expose open var value: Node?,
    @Expose open var children:RealmList<RawTreeNode>,
    @Expose open var parent: RawTreeNode?=null,
    @Expose open var progress:Int=0,
    @Expose open var uuid:String=UUID.randomUUID().toString()
):RealmObject(){
    constructor():this("")
    constructor(str:String,isJson:Boolean=false):
            this(null, RealmList<RawTreeNode>()) {
        if (isJson) {
            val result = Gson().fromJson(str, this::class.java)!!
            //TODO:json->realm
            this.value = result.value
            this.children = result.children
            this.parent = result.parent
            this.progress = result.progress
            this.uuid = result.uuid
        }else{
        }
    }
    constructor(seedRoot:TreeSeedNode,parent: RawTreeNode?=null):this(""){
        this.value=seedRoot.value
        this.progress=0
        this.parent=parent
        this.uuid=UUID.randomUUID().toString()
        seedRoot.children.forEach {
            this.children.add(RawTreeNode(it,this))
        }
    }


    fun toJson():String{
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        return gson.toJson(this)
    }

    fun toViewTreeNode(): ViewTreeNode {
        return ViewTreeNode(Node(""))
    }

}