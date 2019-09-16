package com.f3401pal.checkabletreeview

import com.google.gson.annotations.Expose
import java.util.*

open class Node(
    @Expose val str: String,
    @Expose val type:String=NodeTypes.NODE.name,
    @Expose val notice: Date?=null,
    @Expose val sharedId:Int?=null,
    @Expose val mediaUri:String?=null,
    @Expose val detail:MutableMap<String,String>?=null,
    @Expose val link:String?=null,
    @Expose val power:Int=1
) : Checkable(false) {
    override fun toString(): String {
        return str
    }
}