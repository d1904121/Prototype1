package com.example.prototype1.views
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype1.checkabletreeview.views.SingleRecyclerViewImpl
import com.example.prototype1.utils.AppUtils
import com.example.prototype1.utils.NodeUtils
import com.example.prototype1.utils.UserUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //定義わすれずに
    private lateinit var treeView: SingleRecyclerViewImpl
    private lateinit var realm: Realm
    private lateinit var auth: FirebaseAuth
    var user:FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.prototype1.R.layout.activity_main)

        //【 ローカル保存】 のためのrealmの取得
        realm=AppUtils().getRealm(this)

        //木構造を表示するリスト
        treeView = findViewById(com.example.prototype1.R.id.treeView)

        treeBtn.setOnClickListener {
            //木の取得と画面への反映
            val root=NodeUtils().getRoot(realm)
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
        }

        //項目がクリックされるとき、何をするかを設定
        treeView.setItemOnClick { treeNode, viewHolder ->

        }

        //【ユーザーシステム】のためのFirebase　Authの取得
        auth = FirebaseAuth.getInstance()
        loginBtn.setOnClickListener {
            //ログイン
            val uu=UserUtils(this,auth)
            uu.login("user@email.com","password",
                callback = {
                    Toast.makeText(this,"user-uid:${it?.uid?:"null"}", Toast.LENGTH_SHORT).show()
                    user=it

                    //ユーザー登録はcreateUser、使い方は一緒
                    //ユーザーの属性の設定と取得、設定してないときはnullになる、
                    //取得後の操作はcallbackでやる
                    // TODO: async, await
                    uu.set("teacher","Prof.H")
                    uu.get("teacher",String::class.java,callback ={
                        Toast.makeText(this,"teacher:${it?:"null"}", Toast.LENGTH_SHORT).show()
                    })
                })
        }



    }

    override fun onDestroy() {
        //onDestroyに必ずこれつけてください
        realm.close()
        super.onDestroy()
    }
}
