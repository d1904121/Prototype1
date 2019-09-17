package com.example.prototype1.checkabletreeview.models
import com.example.prototype1.checkabletreeview.views.Checkable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import io.realm.RealmObject
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
    @Expose override var checked: Boolean=false
) : Checkable, RealmObject() {
    constructor(str:String,isJson:Boolean=false):this(){
        if(isJson){
            val result=Gson().fromJson(str,Node::class.java)!!
            this.str=result.str
            this.type=result.type
            this.notice=result.notice
            this.sharedId=result.sharedId
            this.mediaUri=result.mediaUri
            this.link=result.link
            this.power=result.power
            this.checked=result.checked
        }
    }
//    override fun toString(): String {
//        return str
//    }

    fun toJson():String{
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        return gson.toJson(this)
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