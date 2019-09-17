package com.f3401pal.checkabletreeview.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.f3401pal.checkabletreeview.R
import com.f3401pal.checkabletreeview.models.Node
import com.f3401pal.checkabletreeview.models.NodeTypes
import com.f3401pal.checkabletreeview.models.ViewTreeNode
import com.f3401pal.checkabletreeview.utils.px
import kotlinx.android.synthetic.main.item_checkable_text.view.*
import kotlinx.android.synthetic.main.item_checkable_text.view.expandIndicator
import kotlinx.android.synthetic.main.item_checkable_text.view.indentation
import kotlinx.android.synthetic.main.item_quick_create_node.view.*
import kotlinx.android.synthetic.main.item_text_only.view.*

private const val TAG = "SingleRecyclerView"
class SingleRecyclerViewImpl : RecyclerView,
    CheckableTreeView<Node> {
    private val adapter: TreeAdapter by lazy {
        val indentation = indentation.px
        TreeAdapter(
            indentation,
            this
        )
    }
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
            viewNodes=nodesList
            notifyDataSetChanged()
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
        viewHolder.itemView.expandIndicator.startToggleAnimation(node.isExpanded)
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
        val node=viewNodes[position]
        return when(node.value){
//            is TestNode -> NodeTypes.TEST_NODE.ordinal
//            is QuickCreateNode -> NodeTypes.QUICK_CREATE_NODE.ordinal
            //TODO: add your node type here
            else -> NodeTypes.NODE.ordinal
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout=when(viewType){
            //TODO: add your item layout here
//            NodeTypes.TEST_NODE.ordinal -> R.layout.item_text_only
//            NodeTypes.QUICK_CREATE_NODE.ordinal -> R.layout.item_quick_create_node
            else -> R.layout.item_checkable_text
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false), indentation,recyclerView)
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
    inner class ViewHolder(view: View, private val indentation: Int, recyclerView: SingleRecyclerViewImpl)
        : RecyclerView.ViewHolder(view) {

        private fun bindIndentation(viewNode: ViewTreeNode){
            itemView.indentation.minimumWidth = indentation * viewNode.getLevel()
        }
        private fun bindExpandIndicator(viewNode: ViewTreeNode){
            if(viewNode.isLeaf()) {
                itemView.expandIndicator.visibility = View.GONE
            } else {
                itemView.expandIndicator.visibility = View.VISIBLE
                itemView.expandIndicator.setOnClickListener { expandCollapseToggleHandler(viewNode, this) }
                itemView.expandIndicator.setIcon(viewNode.isExpanded)
            }
        }
        private fun bindCommon(viewNode: ViewTreeNode){
            bindIndentation(viewNode)
            bindExpandIndicator(viewNode)
        }
        private fun bindCheckableText(viewNode: ViewTreeNode){
            bindCommon(viewNode)
            itemView.checkText.text = viewNode.value.toString()
            itemView.checkText.setOnCheckedChangeListener(null)
            val state = viewNode.getCheckedStatus()
            itemView.checkText.isChecked = state.allChildrenChecked
            itemView.checkText.setIndeterminate(state.hasChildChecked)
            itemView.checkText.setOnCheckedChangeListener { _, isChecked ->
                viewNode.setChecked(isChecked)
                notifyDataSetChanged()
            }

        }
        private fun bindTextOnly(viewNode: ViewTreeNode){
            bindCommon(viewNode)
            itemView.textView.text = viewNode.value.toString()
            itemView.setOnClickListener {
                itemOnclick(viewNode,this)
            }
        }
        private fun bindQuickCreateNode(viewNode: ViewTreeNode){
            bindIndentation(viewNode)
            itemView.createButton.setOnClickListener {
                if(viewNode.parent != null) {
                    val str = itemView.editText.text.toString()
                    val newNode = ViewTreeNode(
                        Node(str),
                        viewNode.parent as ViewTreeNode
                    )
                    (viewNode.parent as ViewTreeNode).children.add(newNode)
                    viewNode.parent.children.remove(viewNode )
                    viewNode.parent.children.add(viewNode )
                    itemView.editText.setText("")
                    //TODO: enter->create and hide keyboard
                    recyclerView.setRoots(mutableListOf(viewNode.getRoot()))
                }
            }
        }
        //TODO: create your bind function here, do not forget setOnClickListener
        internal fun bind(viewNode: ViewTreeNode) {
            when(viewNode.value){
                //TODO: bind your layout here
//                is TestNode -> bindTextOnly(viewNode)
//                is QuickCreateNode -> bindQuickCreateNode(viewNode)
                else -> bindCheckableText(viewNode)
            }
        }

    }
}
