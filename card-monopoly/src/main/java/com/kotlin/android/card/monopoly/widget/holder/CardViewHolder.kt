package com.kotlin.android.card.monopoly.widget.holder

import android.view.View
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.ViewPagerAdapter
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlinx.android.synthetic.main.frag_card.view.*

/**
 * 卡片ViewHolder
 *
 * Created on 2020/8/31.
 *
 * @author o.s
 */
class CardViewHolder(view: View) : ViewPagerAdapter.ViewHolder(view) {

    init {
        initView()
    }

    override fun bindData() {
    }

    private fun initView() {
        itemView.checkCardView?.apply {
            limit = 12
            spec = CheckCardView.Spec.CARD
        }
        itemView.composeView?.apply {
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 18.dpF
            )
        }
        itemView.discardView?.apply {
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 18.dpF
            )
        }
        itemView.saveCofferView?.apply {
            setBackground(
                    colorRes = R.color.color_12c7e9,
                    cornerRadius = 18.dpF
            )
        }
    }

}