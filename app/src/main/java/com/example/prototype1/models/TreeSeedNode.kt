package com.example.prototype1.models

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.annotations.Expose
import java.util.*

class TreeSeedNode(
    @Expose var value: Node,
    @Expose var children:MutableList<TreeSeedNode>,
    @Expose var parent: TreeSeedNode?,
    @Expose var uuid:String= UUID.randomUUID().toString()
) {
    constructor():this(Node(""), mutableListOf<TreeSeedNode>(),null)
    constructor(raw: RawTreeNode,parent: TreeSeedNode?):this(){
        this.value=raw.value!!
        this.parent=parent
        this.children.clear()
        raw.children.forEach {
            this.children.add(TreeSeedNode(it,this))
        }
    }
    fun upload(){
        val ref = FirebaseDatabase.getInstance().getReference("seeds")

        //TODO:fix
        ref.push().setValue(this)
    }

    fun download(key:String,callback:(TreeSeedNode?)->Unit, cancelled:(DatabaseError)->Unit={}){
        val ref=FirebaseDatabase.getInstance().getReference("seeds/$key")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.getValue(TreeSeedNode::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                cancelled(error)
            }
        })
    }
}