package com.example.prototype1.views
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype1.checkabletreeview.views.SingleRecyclerViewImpl
import com.example.prototype1.utils.AppUtils
import com.example.prototype1.utils.NodeUtils
import io.realm.Realm


class MainActivity : AppCompatActivity() {
    private lateinit var treeView: SingleRecyclerViewImpl
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.prototype1.R.layout.activity_main)

        //realmの取得
        realm=AppUtils().getRealm(this)

        //木の取得と画面への反映
        val root=NodeUtils().getRoot(realm)
        treeView = findViewById(com.example.prototype1.R.id.treeView)
        NodeUtils().refreshView(treeView,root)

        //RawNodeの編集、executeTransactionで、
        //または realm.beginTransaction()とrealm.commitTransaction()
        //で囲んでください
//        realm.executeTransaction {
//            root.value!!.str="root2"
//            root.children.add(RawTreeNode(Node("l21")))
//        }
        //画面への反映を忘れずに
//        NodeUtils().refreshView(treeView,root)

        treeView.setItemOnClick { treeNode, viewHolder ->

        }



    }

    override fun onDestroy() {
        //onDestroyに必ずこれつけてください
        realm.close()
        super.onDestroy()
    }
}
