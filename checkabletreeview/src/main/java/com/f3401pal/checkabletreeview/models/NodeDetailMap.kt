package com.f3401pal.checkabletreeview.models

import com.google.gson.annotations.Expose
import io.realm.RealmList
import io.realm.RealmObject


open class NodeDetail(
    @Expose open var key:String,
    @Expose open var value:String?
):RealmObject(){
    constructor():this("","")
}

open class NodeDetailMap(
    @Expose open var list:RealmList<NodeDetail>
): RealmObject() {
    constructor():this(RealmList())
    fun get(key:String):String?{
        return list.findLast { it.key==key }?.value
    }
    fun set(key:String,value:String?){
        val detail=list.findLast { it.key==key }
        if(detail != null){
            detail.value=value
        }else{
            list.add(NodeDetail(key,value))
        }
    }
    fun unset(key:String){
        set(key,null)
    }
}