package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.RewardPropInfo
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlinx.android.synthetic.main.view_get_reward.view.*

/**
 * 获得奖励视图：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class RewardView : FrameLayout {

    var data: Data? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
            cardImageView?.apply {
                card = Card(cardCover = rewardToolInfo?.toolCover)
            }
            val count = if (rewardToolInfo != null) {
                1
            } else {
                0
            }
            countView?.text = getString(R.string.x_, count)
            coinView?.text = rewardGold.toString()
            if (goldDesc == null) {
                coinLabelView?.visible()
                goldLabelView?.gone()
                divideView?.gone()
                descView?.gone()
                descView?.text = ""
            } else {
                coinLabelView?.gone()
                goldLabelView?.visible()
                divideView?.visible()
                descView?.visible()
                descView?.text = goldDesc
            }
        } ?: resetUI()
    }

    private fun resetUI() {
        countView?.text = getString(R.string.x_, 0)
        coinView?.text = ""
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        addView(inflate(context, R.layout.view_get_reward, null))

        labelView?.apply {
            setBackground(
                    colorRes = R.color.color_91d959,
                    cornerRadius = 4.dpF
            )
        }

    }

    data class Data(
            var rewardToolInfo: RewardPropInfo? = null,
            var rewardGold: Long = 0L, // 实得金币
            var goldDesc: CharSequence? = null, // 合成套装奖励金币描述信息
    )

}