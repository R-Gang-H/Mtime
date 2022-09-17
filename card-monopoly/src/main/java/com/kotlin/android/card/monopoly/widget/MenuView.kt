package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 大富翁菜单栏：我的口袋、我的套装、我的道具、我的卡友、卡片商店、拍卖行、许愿树、幸运转盘
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class MenuView : FrameLayout {

    private val mIconWidth = 40.dp
    private val mIconHeight = 47.dp
    private val mMarginTop = 8.dp
    private val mMarginOtherTop = 28.dp
    private val mMargin = 20.dp
    private val mMarginUnit = mIconHeight + mMargin

    private var pocketView: ImageView? = null
    private var suitView: ImageView? = null
    private var propView: ImageView? = null
    private var friendView: ImageView? = null
    private var shopView: ImageView? = null
    private var auctionView: ImageView? = null
    private var wishingTreeView: ImageView? = null
    private var messageCenterView: ImageView? = null
    private var luckyDrawView: ImageView? = null

    var action: ((type: ActionType) -> Unit)? = null

    var style: Style = Style.SELF
        set(value) {
            field = value
            notifyChange()
        }

    /**
     * true：朋友关系
     */
    var friendRelation: Boolean? = true
        set(value) {
            field = value
            updateAddFriendView()
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
        style = Style.SELF
    }

    private fun initView(@DrawableRes iconRes: Int, type: ActionType, gravity: Int, margin: Int = 0): ImageView {
        return ImageView(context).apply {
            LayoutParams(mIconWidth, mIconHeight).apply {
                this.gravity = gravity
                topMargin = margin
                layoutParams = this
            }
            setImageResource(iconRes)
            setOnClickListener {
                action?.invoke(type)
            }
            addView(this)
        }
    }

    private fun notifyChange() {
        when (style) {
            Style.SELF -> {
                fillSelf()
            }
            Style.FRIEND -> {
                fillFriend()
            }
        }
    }

    private fun fillSelf() {
        removeAllViews()
        pocketView = initView(R.mipmap.ic_pocket, ActionType.POCKET, Gravity.START, mMarginTop)
        suitView = initView(R.mipmap.ic_suit, ActionType.SUIT, Gravity.START, mMarginTop + mMarginUnit * 1)
        propView = initView(R.mipmap.ic_prop, ActionType.PROP, Gravity.START, mMarginTop + mMarginUnit * 2)
        friendView = initView(R.mipmap.ic_card_friend, ActionType.FRIEND, Gravity.START, mMarginTop + mMarginUnit * 3)

        shopView = initView(R.mipmap.ic_card_shop, ActionType.SHOP, Gravity.END, mMarginTop)
        auctionView = initView(R.mipmap.ic_auction, ActionType.AUCTION, Gravity.END, mMarginTop + mMarginUnit * 1)
        wishingTreeView = initView(R.mipmap.ic_wishing_tree, ActionType.WISHING_TREE, Gravity.END, mMarginTop + mMarginUnit * 2)
        messageCenterView = initView(R.mipmap.ic_message_center, ActionType.MESSAGE_CENTER, Gravity.END, mMarginTop + mMarginUnit * 3)
//        luckyDrawView = initView(R.mipmap.ic_lucky_draw, ActionType.LUCKY_DRAW, Gravity.END, mMarginTop + mMarginUnit * 3)
    }

    private fun fillFriend() {
        removeAllViews()
        pocketView = initView(R.mipmap.ic_ta_pocket, ActionType.TA_POCKET, Gravity.START, mMarginOtherTop)
        suitView = initView(R.mipmap.ic_ta_suit, ActionType.TA_SUIT, Gravity.START, mMarginOtherTop + mMarginUnit * 1)
        wishingTreeView = initView(R.mipmap.ic_ta_wishing_tree, ActionType.TA_WISHING_TREE, Gravity.START, mMarginOtherTop + mMarginUnit * 2)

        friendView = initView(R.mipmap.ic_add_friend, ActionType.ADD_FRIEND, Gravity.END, mMarginOtherTop + mMarginUnit * 2)
        updateAddFriendView()
    }

    private fun updateAddFriendView() {
        friendView?.apply {
            if (friendRelation == true) {
                gone()
            } else {
                visible()
            }
        }
    }

    enum class Style {

        /**
         * 我的菜单栏
         */
        SELF,

        /**
         * 卡友的菜单栏
         */
        FRIEND;
    }

    /**
     * 点击动作类型
     */
    enum class ActionType {
        POCKET,
        SUIT,
        PROP,
        FRIEND,

        SHOP,
        AUCTION,
        WISHING_TREE,
        LUCKY_DRAW,
        MESSAGE_CENTER,

        TA_POCKET,
        TA_SUIT,
        TA_WISHING_TREE,
        ADD_FRIEND
    }
}