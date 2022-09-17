package com.kotlin.android.card.monopoly.widget.user

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.getDrawable

/**
 * 用户 buff 视图
 *
 * Created on 2021/5/20.
 *
 * @author o.s
 */
class UserBuffView : LinearLayout {

    private val iconWidth = 16.dp
    private val iconMargin = 1.dp

    var data: ArrayList<BuffType>? = null
        set(value) {
            field = value
            fillData()
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
        orientation = HORIZONTAL
    }

    private fun fillData() {
        clear()
        data?.forEach {
            addView(createBuffView(it))
        }
    }

    private fun createBuffView(type: BuffType): View {
        return ImageView(context).apply {
            LayoutParams(iconWidth, LayoutParams.WRAP_CONTENT).apply {
                layoutParams = this
                setMargins(iconMargin, 0, iconMargin, 0)
            }
            setImageDrawable(getDrawable(type.resId))
        }
    }

    private fun clear() {
        removeAllViews()
    }
}

/**
 * 对自己使用:
 * 财神卡(1)、流氓卡(2)、防盗卡(4)、运气卡(6)、急救卡(7)、反弹卡(11)
 *
 * 对好友使用:
 * 打劫卡(5)、奴隶卡(3)、黑客卡(9)
 *
 * 功能卡：
 * 复制卡(8)、
 * 恶魔卡(10): 不能直接使用
 */
enum class BuffType(
        val cardId: Int,
        val label: String,
        @DrawableRes val resId: Int,
) {
    WEALTH_CARD(1, "财神卡", R.drawable.ic_buff_wealth_card),
    ROGUE_CARD(2, "流氓卡", R.drawable.ic_buff_rogue_card),
    SLAVE_CARD(3, "奴隶卡", R.drawable.ic_buff_slave_card),
    GUARD_CARD(4, "防盗卡", R.drawable.ic_buff_guard_card),
    ROBBERY_CARD(5, "打劫卡", R.drawable.ic_buff_robbery_card),
    HACKER_CARD(9, "黑客卡", R.drawable.ic_buff_hacker_card),
    BOUNCE_CARD(11, "反弹卡", R.drawable.ic_buff_bounce_card),
    POCKET_CARD(14, "口袋卡", R.drawable.ic_buff_pocket_card),
    HIDE_CARD(25, "隐身卡", R.drawable.ic_buff_hide_card),
}