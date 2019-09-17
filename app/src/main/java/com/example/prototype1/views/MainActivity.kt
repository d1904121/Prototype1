package com.example.prototype1.views
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype1.R
import com.example.prototype1.checkabletreeview.models.Node
import com.example.prototype1.checkabletreeview.models.NodeDetailMap
import com.example.prototype1.checkabletreeview.views.SingleRecyclerViewImpl
import com.example.prototype1.models.RawTreeNode
import com.example.prototype1.utils.AppUtils
import io.realm.Realm

class MainActivity : AppCompatActivity() {
    private lateinit var treeView: SingleRecyclerViewImpl
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm=AppUtils().getRealm(this)
        realm.beginTransaction()
        treeView = findViewById(R.id.treeView)

        //for debug
        realm.deleteAll()

        var root=realm.where(RawTreeNode::class.java).findFirst()
            ?:realm.createObject(RawTreeNode::class.java).apply {
                value=realm.createObject(Node::class.java).apply {
                    str="root"
                    detail=realm.createObject(NodeDetailMap::class.java)
                }
            }


//        val list= mutableListOf(root.toViewTreeNode())
//        treeView.setRoots(list)

        treeView.setItemOnClick { treeNode, viewHolder ->

        }


    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}
