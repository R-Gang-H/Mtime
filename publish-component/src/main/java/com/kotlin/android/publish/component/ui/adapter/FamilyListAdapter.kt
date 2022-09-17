package com.kotlin.android.publish.component.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.community.group.MyGroupList.Group
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.publish.component.R
import kotlinx.android.synthetic.main.item_family_list.view.*

/**
 *
 * Created on 2020/10/14.
 *
 * @author o.s
 */
class FamilyListAdapter(val action: ((data: Group) -> Unit)? = null) : RecyclerView.Adapter<FamilyListAdapter.ViewHolder>() {
    private val data: ArrayList<Group> = ArrayList()

    fun setData(data: ArrayList<Group>) {
        this.data.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.item_family_list, null), action)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until data.size) {
            holder.bindData(data[position])
        }
    }

    class ViewHolder(view: View, val action: ((data: Group) -> Unit)? = null) : RecyclerView.ViewHolder(view) {
        private val mWidth = 80.dp  // dp
        private val mHeight = 80.dp // dp

        fun bindData(data: Group) {
            itemView.iconView?.apply {
                loadImage(data = data.groupImg, width = mWidth, height = mHeight)
            }
            itemView.titleView?.apply {
                text = data.groupName
            }
            itemView.setOnClickListener {
                action?.invoke(data)
            }
        }
    }
}