package com.example.prototype1.models
import com.example.prototype1.checkabletreeview.models.NodeDetailMap
import com.example.prototype1.checkabletreeview.models.NodeTypes
import com.example.prototype1.checkabletreeview.views.Checkable
import com.google.gson.annotations.Expose
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.RealmModule
import java.util.*

@RealmModule(allClasses = true)
open class Node(
    @Expose open var str: String="",
    @Expose open var type:String= NodeTypes.NODE.name,
    @Expose open var notice: Date?=null,
    @Expose open var sharedId:Int?=null,
    @Expose open var mediaUri:String?=null,
    @Expose open var detail: NodeDetailMap?=null,
    @Expose open var link:String?=null,
    @Expose open var power:Int=1,
    @Expose @Ignore override var checked: Boolean=false,
    @Expose open var uuid:String=UUID.randomUUID().toString()
) : Checkable, RealmObject() {

    override fun toString(): String {
        return str
    }

    fun getDetail(key:String):String?{
        return detail?.get(key)
    }

    fun unsetDetail(key:String){
        detail?.unset(key)
    }

    fun setDetail(key:String,value:String?){
        detail?.set(key,value)
    }

}