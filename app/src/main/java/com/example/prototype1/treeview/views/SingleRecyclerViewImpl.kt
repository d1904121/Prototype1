package com.example.prototype1.treeview.views
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prototype1.Tags
import com.example.prototype1.treeview.models.ViewNodeTypes
import com.example.prototype1.treeview.models.ViewNodeUtils
import com.example.prototype1.treeview.models.ViewTreeNode
import com.example.prototype1.treeview.utils.px
import com.example.prototype1.models.Node
import com.example.prototype1.models.RawTreeNode
import io.realm.Realm
import kotlinx.android.synthetic.main.item_checkable_text.view.*
import kotlinx.android.synthetic.main.item_checkable_text.view.indentation
import kotlinx.android.synthetic.main.item_quick_create_node.view.*
import java.util.*

private const val TAG = "SingleRecyclerView"
class SingleRecyclerViewImpl : RecyclerView,
    TreeView<Node> {
    private val adapter: TreeAdapter by lazy {
        val indentation = indentation.px
        TreeAdapter(
            indentation,
            this
        )
    }
    var realm:Realm? = null
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, style: Int) : super(context, attributeSet, style)
    init {
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        setAdapter(adapter)
    }



    fun treeToList(roots: ViewTreeNode):MutableList<ViewTreeNode>{
        val result= mutableListOf<ViewTreeNode>(roots)
        val iterator =result.listIterator()
        for(item in iterator){
            if(item.isExpanded){
                for(child in item.children){
                    treeToList(child).forEach {
                        iterator.add(it)
                    }
                }
            }
        }
        return result
    }
    @UiThread override fun setRoots(roots: MutableList<ViewTreeNode>) {
        with(adapter) {
            val nodesList=mutableListOf<ViewTreeNode>()
            for(root in roots){
                nodesList.addAll(treeToList(root))
            }
            val beforeCount=viewNodes.size
            viewNodes.clear()
            notifyItemRangeRemoved(0,beforeCount)
            viewNodes=nodesList
            notifyItemRangeInserted(0,viewNodes.size)
//            notifyDataSetChanged()
        }
    }

    fun setItemOnClick(click:(ViewTreeNode, TreeAdapter.ViewHolder)->Unit){
        adapter.setItemOnClick(click)
    }
}

class TreeAdapter(private val indentation: Int, private val recyclerView: SingleRecyclerViewImpl) : RecyclerView.Adapter<TreeAdapter.ViewHolder>() {
    internal var viewNodes: MutableList<ViewTreeNode> = mutableListOf()
    private val expandCollapseToggleHandler: (ViewTreeNode, ViewHolder) -> Unit = { node, viewHolder ->
        if(node.isExpanded) {
            collapse(viewHolder.adapterPosition)
        } else {
            expand(viewHolder.adapterPosition)
        }
//        viewHolder.itemView.expandIndicator.startToggleAnimation(node.isExpanded)
    }
    lateinit var itemOnclick:(ViewTreeNode, ViewHolder)->Unit

    init {
        setHasStableIds(true)
    }
    fun setItemOnClick(click:(ViewTreeNode, ViewHolder)->Unit){
        itemOnclick=click
    }
    override fun getItemId(position: Int): Long {
        return viewNodes[position].id
    }
    override fun getItemViewType(position: Int): Int {
        val node = viewNodes[position]
        return node.type.ordinal
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout=ViewNodeUtils().getLayout(viewType)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false), indentation,recyclerView,recyclerView.realm)
    }

    override fun getItemCount(): Int {
        return viewNodes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewNodes[position])
    }
    @UiThread
    private fun expand(position: Int) {
        if(position >= 0) {
            // expand
            val node = viewNodes[position]
            val insertPosition = position + 1
            val insertedSize = node.children.size
            viewNodes.addAll(insertPosition, node.children)
            node.isExpanded = true
            notifyItemRangeInserted(insertPosition, insertedSize)
        }
    }
    @UiThread
    private fun collapse(position: Int) {
        // collapse
        if(position >= 0) {
            val node = viewNodes[position]
            var removeCount = 0
            fun removeChildrenFrom(cur: ViewTreeNode) {
                viewNodes.remove(cur)
                removeCount++
                if(cur.isExpanded) {
                    cur.children.forEach { removeChildrenFrom(it) }
                    cur.isExpanded = false
                }
            }
            node.children.forEach { removeChildrenFrom(it) }
            node.isExpanded = false
            notifyItemRangeRemoved(position + 1, removeCount)
        }
    }

    inner class ViewHolder(view: View, private val indentation: Int, recyclerView: SingleRecyclerViewImpl,val realm: Realm?)
        : RecyclerView.ViewHolder(view) {

        private fun bindIndentation(viewNode: ViewTreeNode){
            itemView.indentation.minimumWidth = indentation * viewNode.getLevel()
        }
//        private fun bindExpandIndicator(viewNode: ViewTreeNode){
//            if(viewNode.isLeaf()) {
//                itemView.expandIndicator.visibility = View.GONE
//            } else {
//                itemView.expandIndicator.visibility = View.VISIBLE
//                itemView.expandIndicator.setOnClickListener { expandCollapseToggleHandler(viewNode, this) }
//                itemView.expandIndicator.setIcon(viewNode.isExpanded)
//            }
//        }
        private fun bindCommon(viewNode: ViewTreeNode){
            bindIndentation(viewNode)
//            bindExpandIndicator(viewNode)
        }
        private fun bindCheckableText(viewNode: ViewTreeNode){
            bindCommon(viewNode)

            itemView.checkText.text = viewNode.value.toString()
            itemView.checkText.setOnCheckedChangeListener(null)
            itemView.checkText.isChecked=viewNode.rawReference?.progress?:0>0
            itemView.checkText.setIndeterminate(viewNode.rawReference?.progress?:0>0)

            itemView.rightView.setOnClickListener {
//                itemOnclick(viewNode,this)//detail
                expandCollapseToggleHandler(viewNode, this)
            }

            itemView.checkText.setOnCheckedChangeListener { _, isChecked ->



//                if (realm != null) {
//                    AppUtils().executeTransactionIfNotInTransaction(realm){
//                        if(viewNode.rawReference?.progress?:0>0){
//                            viewNode.rawReference?.progress=0
//                        }else{
//                            viewNode.rawReference?.progress=1
//                        }
//                        viewNode.setChecked(viewNode.rawReference?.progress?:0>0,realm,viewNode)
//
//                        if(viewNode.parent!=null){
//                            var parent=viewNode.parent
//                            var state: NodeCheckedStatus?
//                            while(parent!=null && realm != null) {
//                                state=parent.getCheckedStatus()
//                                AppUtils().executeTransactionIfNotInTransaction(realm) {
//                                    parent!!.rawReference?.progress =
//                                        if (state.allChildrenChecked) 1 else 0
//                                    parent!!.value.checked = state.allChildrenChecked
//                                }
//                                parent=parent.parent
//                            }
//                        }
//                    }
//                    notifyDataSetChanged()
//                }
            }
        }
        private fun bindQuickCreateNode(viewNode: ViewTreeNode){
            bindIndentation(viewNode)
            //TODO: enter->create and hide keyboard
            itemView.createButton.setOnClickListener {
                if(itemView.textView.text.toString().isEmpty()){
                    Toast.makeText(recyclerView.context,"Please input something",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(viewNode.parent != null) {
                    //get variables
                    val inputStr = itemView.textView.text.toString()
                    val viewParent=viewNode.parent as ViewTreeNode
                    var newNode:RawTreeNode?=null
                    if(viewNode.parent!=null && realm!=null) {
                        //create new RawNode
                        realm.executeTransaction {
                            newNode=realm.createObject(RawTreeNode::class.java,
                                UUID.randomUUID().toString()).apply {
                                value=realm.createObject(Node::class.java).apply {
                                    //set new node
                                    str=inputStr
                                }
                                parent=viewParent.rawReference
                            }
                            viewParent.rawReference?.children?.add((newNode))

                            viewParent.children.remove(viewNode)
                            viewNodes.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition+1)
                            val newViewNode=ViewTreeNode(newNode!!,viewParent,null)
                            viewParent.children.add(newViewNode)
                            viewNodes.add(adapterPosition,newViewNode)
                            viewParent.children.add(viewNode)
                            viewNodes.add(adapterPosition+1,viewNode)
                            notifyItemRangeInserted(adapterPosition+1,2)
                            itemView.textView.setText("")
                        }
                    }else{
                        Log.w(Tags.DEFAULT.name, "SingleRecyclerViewImpl:realm not set, or parent does not exist")
                    }
                }
            }
        }
        //TODO: create your bind function here, do not forget setOnClickListener
        internal fun bind(viewNode: ViewTreeNode) {
            when(viewNode.type){
                ViewNodeTypes.QUICK_CREATE_NODE -> bindQuickCreateNode(viewNode)
                else -> bindCheckableText(viewNode)
            }
        }

    }
}