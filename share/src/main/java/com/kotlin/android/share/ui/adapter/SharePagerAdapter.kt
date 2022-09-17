package com.kotlin.android.share.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.kotlin.android.share.R
import com.kotlin.android.share.SharePlatform
import kotlinx.android.synthetic.main.item_share_view.view.*

/**
 *
 * Created on 2020/6/30.
 *
 * @author o.s
 */
class SharePagerAdapter(private val context: Context, private val click: (platform: SharePlatform) -> Unit) : PagerAdapter() {

    /**
     * 一页有多个分享单元，当前实现是 4 个。配合UI布局设置
     */
    private val data = ArrayList<ArrayList<SharePlatform>>()

    fun setData(data: List<ArrayList<SharePlatform>>) {
        if (data.isNotEmpty()) {
            this.data.clear()
            this.data.addAll(data)
            notifyDataSetChanged()
        }
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by [.instantiateItem]. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view Page View to check for association with `object`
     * @param any Object to check for association with `view`
     * @return true if `view` is associated with the key object `object`
     */
    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == (any as ViewHolder).itemView
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int = data.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = View.inflate(context, R.layout.item_share_view, null)
        val holder = ViewHolder(view, click)
        holder.bindData(data[position])
        container.addView(view)
        return holder
    }

    private class ViewHolder(val itemView: View, val click: (platform: SharePlatform) -> Unit) {

        fun bindData(data: ArrayList<SharePlatform>) {
            resetView()
            val size = data.size
            if (size > 0) {
                itemView.share0.visibility = View.VISIBLE
                val platform = data[0]
                itemView.icon0.setImageResource(platform.icon)
                itemView.title0.setText(platform.title)
                itemView.share0.setOnClickListener {
                    click(platform)
                }
            }
            if (size > 1) {
                itemView.share1.visibility = View.VISIBLE
                val platform = data[1]
                itemView.icon1.setImageResource(platform.icon)
                itemView.title1.setText(platform.title)
                itemView.share1.setOnClickListener {
                    click(platform)
                }
            }
            if (size > 2) {
                itemView.share2.visibility = View.VISIBLE
                val platform = data[2]
                itemView.icon2.setImageResource(platform.icon)
                itemView.title2.setText(platform.title)
                itemView.share2.setOnClickListener {
                    click(platform)
                }
            }
            if (size > 3) {
                itemView.share3.visibility = View.VISIBLE
                val platform = data[3]
                itemView.icon3.setImageResource(platform.icon)
                itemView.title3.setText(platform.title)
                itemView.share3.setOnClickListener {
                    click(platform)
                }
            }
        }

        private fun resetView() {
            itemView.share0.visibility = View.INVISIBLE
            itemView.share1.visibility = View.INVISIBLE
            itemView.share2.visibility = View.INVISIBLE
            itemView.share3.visibility = View.INVISIBLE
        }
    }
}
