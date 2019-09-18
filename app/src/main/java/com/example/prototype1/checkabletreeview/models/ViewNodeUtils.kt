package com.example.prototype1.checkabletreeview.models

import com.example.prototype1.R

enum class ViewNodeTypes {
    //TODO: add your node type here
    NODE,
    QUICK_CREATE_NODE,
}

class ViewNodeUtils{
    fun getLayout(type:Int):Int{
        return when(type){
            //TODO: add your layout here
            ViewNodeTypes.NODE.ordinal->R.layout.item_checkable_text
            ViewNodeTypes.QUICK_CREATE_NODE.ordinal->R.layout.item_quick_create_node
            else-> R.layout.item_checkable_text
        }
    }
}