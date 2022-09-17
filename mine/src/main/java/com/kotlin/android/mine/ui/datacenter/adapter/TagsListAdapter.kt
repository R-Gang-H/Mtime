package com.kotlin.android.mine.ui.datacenter.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import kotlinx.android.synthetic.main.mine_item_tag_data_center.view.*

/**
 *
 * @Package:        com.kotlin.android.mine.ui.datacenter.adapter
 * @ClassName:      haoruigang
 * @Description:    数据中心Tags
 * @Author:         haoruigang
 * @CreateDate:     2022/3/11 15:27
 * @Version:
 */
class TagsListAdapter(val action: ((data: DataCenterViewBean.Tags, pos: Int, adapter: TagsListAdapter) -> Unit)? = null) :
    RecyclerView.Adapter<TagsListAdapter.ViewHolder>() {
    private val data: ArrayList<DataCenterViewBean.Tags> = ArrayList()

    fun setData(data: ArrayList<DataCenterViewBean.Tags>) {
        this.data.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.mine_item_tag_data_center, null), action)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until data.size) {
            holder.bindData(data[position], position, this)
        }
    }

    class ViewHolder(
        view: View,
        val action: ((data: DataCenterViewBean.Tags, pos: Int, adapter: TagsListAdapter) -> Unit)? = null,
    ) :
        RecyclerView.ViewHolder(view) {

        val TAB_CORNER_RADIUS = 15 // dp

        fun bindData(data: DataCenterViewBean.Tags, pos: Int, adapter: TagsListAdapter) {
            itemView.tvTag?.apply {
                text = data.tagName
                if (data.isSelect) {
                    ShapeExt.setShapeColorAndCorner(this, R.color.color_20a0da_alpha_10, TAB_CORNER_RADIUS)
                    setTextColor(getColor(R.color.color_20a0da))
                } else {
                    ShapeExt.setShapeColorAndCorner(this, R.color.color_f2f3f6, TAB_CORNER_RADIUS)
                    setTextColor(getColor(R.color.color_8798af))
                }
            }
            itemView.setOnClickListener {
                action?.invoke(data, pos, adapter)
            }
        }
    }
}