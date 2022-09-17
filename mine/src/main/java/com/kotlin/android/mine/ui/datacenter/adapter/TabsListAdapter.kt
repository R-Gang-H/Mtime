package com.kotlin.android.mine.ui.datacenter.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import kotlinx.android.synthetic.main.mine_item_tab_data_center.view.*

/**
 *
 * @Package:        com.kotlin.android.mine.ui.datacenter.adapter
 * @ClassName:      haoruigang
 * @Description:    数据中心Tabs
 * @Author:         haoruigang
 * @CreateDate:     2022-03-14 14:25:38
 * @Version:
 */
class TabsListAdapter(val action: ((data: ArrayList<DataCenterViewBean.Tabs>, pos: Int, adapter: TabsListAdapter) -> Unit)? = null) :
    RecyclerView.Adapter<TabsListAdapter.ViewHolder>() {
    private val data: ArrayList<DataCenterViewBean.Tabs> = ArrayList()

    fun setData(data: ArrayList<DataCenterViewBean.Tabs>) {
        this.data.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.mine_item_tab_data_center, null),
            action)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until data.size) {
            holder.bindData(data, position, this)
        }
    }

    class ViewHolder(
        view: View,
        val action: ((data: ArrayList<DataCenterViewBean.Tabs>, pos: Int, adapter: TabsListAdapter) -> Unit)? = null,
    ) :
        RecyclerView.ViewHolder(view) {

        val TAB_CORNER_RADIUS = 6 // dp

        fun bindData(data: ArrayList<DataCenterViewBean.Tabs>, pos: Int, adapter: TabsListAdapter) {
            itemView.tabBg?.apply {
                if (data[pos].isSelect) {
                    ShapeExt.setShapeColorAndCorner(this, R.color.color_20a0da, TAB_CORNER_RADIUS)
                } else {
                    ShapeExt.setShapeColorAndCorner(this, R.color.color_ffffff)
                }
            }
            itemView.tvAmount?.apply {
                text = data[pos].tabName
                if (data[pos].isSelect) {
                    setTextColor(getColor(R.color.color_ffffff))
                } else {
                    setTextColor(getColor(R.color.color_8798af))
                }
            }
            itemView.tvNum?.apply {
                text = data[pos].tabNum.toString()
                if (data[pos].isSelect) {
                    setTextColor(getColor(R.color.color_ffffff))
                } else {
                    setTextColor(getColor(R.color.color_303a47))
                }
            }
            itemView.v_line?.apply {
                if (data.size - 1 > pos) {
                    setBackgroundColor(getColor(R.color.color_f2f3f6))
                }
                if (data.size - 1 == pos) {
                    setBackgroundColor(getColor(R.color.color_ffffff))
                }
                if (data[pos].isSelect) {
                    setBackgroundColor(getColor(R.color.color_20a0da))
                }
            }

            itemView.setOnClickListener {
                action?.invoke(data, pos, adapter)
            }

        }
    }
}