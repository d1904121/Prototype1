package com.example.prototype1.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype1.R
import com.example.prototype1.VariableNames
import com.example.prototype1.models.RawTreeNode
import com.example.prototype1.utils.AppUtils
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_node_detail.*

class NodeDetailActivity : AppCompatActivity() {
    private lateinit var uuid:String
    private lateinit var realm: Realm
    private var node:RawTreeNode?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node_detail)

        uuid=intent.getStringExtra(VariableNames.NODE_UUID.name)
        realm=AppUtils().getRealm(this)

        node=realm.where(RawTreeNode::class.java).equalTo("uuid",uuid).findFirst()

        if(node!=null){
            val value= node!!.value
            titleText.text=value!!.str
            detailText.text=""
            detailText.append("type=${value.type}\n")
            detailText.append("notice=${node!!.notice}\n")
            detailText.append("sharedId=${node!!.sharedId}\n")
            detailText.append("mediaUri=${value.mediaUri}\n")
            detailText.append("detail=${value.detail}\n")
            detailText.append("link=${value.link}\n")
            detailText.append("power=${value.power}\n")
            detailText.append("node-uuid=${value.uuid}\n\n")

            detailText.append("tree-node-uuid=${node!!.uuid}\n")
            detailText.append("parent=${node!!.parent?.value?.str}\n")
            detailText.append("children-numver=${node!!.children.size}\n")
            detailText.append("progress=${node!!.progress}%\n")

        }else{
            titleText.text="err"
            detailText.text="node not found"
        }
    }
}
