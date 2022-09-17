package com.kotlin.android.card.monopoly.widget.holder

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.coffer.tab.CofferTabAdapter
import com.kotlin.android.card.monopoly.adapter.ViewPagerAdapter
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import kotlinx.android.synthetic.main.frag_coffer.view.*

/**
 * 保险箱ViewHolder
 *
 * Created on 2020/8/31.
 *
 * @author o.s
 */
class CofferViewHolder(view: View) : ViewPagerAdapter.ViewHolder(view) {

    private val mAdapter by lazy {
        CofferTabAdapter().apply {
            itemView.recyclerView?.apply {
                action = {
                    if (it.left <= 60.dp) {
                        smoothScrollBy(-it.width, 0)
                    } else if (it.right >= screenWidth - 60.dp) {
                        smoothScrollBy(it.width, 0)
                    }
                }
            }
        }
    }

    init {
        initView()
    }

    override fun bindData() {
    }

    private fun initView() {
        itemView.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            addItemDecoration(CofferTabItemDecoration())
            adapter = mAdapter
        }
        itemView.checkCardView?.apply {
            limit = 6
            post {
                spec = CheckCardView.Spec.COFFER
            }
        }
        itemView.putPocketView?.apply {
            setBackground(
                    colorRes = R.color.color_12c7e9,
                    cornerRadius = 18.dpF
            )
        }
    }

}

class CofferTabItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter?.itemCount ?: 0
        when (position) {
            0 -> {
                outRect.left = 10.dp
            }
            count - 1 -> {
                outRect.left = 5.dp
                outRect.right = 10.dp
            }
            else -> {
                outRect.left = 5.dp
            }
        }
    }

}