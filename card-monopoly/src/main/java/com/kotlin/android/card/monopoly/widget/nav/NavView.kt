package com.kotlin.android.card.monopoly.widget.nav

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.DrawableTextView
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput

/**
 * 导航视图：
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class NavView : LinearLayout {

    private var mItemWidth = 110.dp
    private val mItemHeight = 37.dp
    private val mLabelWidth = 18.dp
    private val mLabelHeight = mLabelWidth
    private var mTextSize = 13F
    private var mHighlightTextSize = 15F

    private var leftView: DrawableTextView? = null
    private var centerView: DrawableTextView? = null
    private var rightView: DrawableTextView? = null
    private var currentView: DrawableTextView? = null
    private var mPosition: Int = 0

    var action: ((position: Int) -> Unit)? = null

    var style: Style = Style.TRIPLE
        set(value) {
            field = value
            notifyChange()
        }

    val position: Int
        get() = mPosition

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

        setBackground(
                colorRes = R.color.color_20a0da,
                cornerRadius = 6.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )

        // 默认添3个导航栏
        leftView = addTextView()
        centerView = addTextView()
        rightView = addTextView()
    }

    /**
     * 设置导航项目
     */
    fun setItems(vararg category: Category) {
        when (category.size) {
            1-> {
                bindItem(leftView, category[0])
                style = Style.ONE
            }
            2 -> {
                bindItem(leftView, category[0])
                bindItem(rightView, category[1])
                style = Style.DOUBLE
            }
            3 -> {
                bindItem(leftView, category[0])
                bindItem(centerView, category[1])
                bindItem(rightView, category[2])
                style = Style.TRIPLE
            }
        }
    }

    /**
     * 选择导航项目
     */
    fun selectItem(position: Int) {
        mPosition = position
        when (style) {
            Style.ONE -> {
                when (position) {
                    0 -> select(leftView, getShapeDrawable(
                            colorRes = R.color.color_ffffff,
                            cornerRadius = 6.dpF,
                            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
                    ))
                }
            }
            Style.DOUBLE -> {
                when (position) {
                    0 -> select(leftView, R.drawable.ic_nav_left_2)
                    1 -> select(rightView, R.drawable.ic_nav_right_2)
                }
            }
            Style.TRIPLE -> {
                when (position) {
                    0 -> select(leftView, R.drawable.ic_nav_left_3)
                    1 -> select(centerView, R.drawable.ic_nav_middle_3)
                    2 -> select(rightView, R.drawable.ic_nav_right_3)
                }
            }
        }
        hideSoftInput()
        action?.invoke(position)
    }

    /**
     * 添加
     */
    private fun addTextView(): DrawableTextView {
        return DrawableTextView(context).apply {
            LayoutParams(mItemWidth, mItemHeight).apply {
                layoutParams = this
            }
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setOnClickListener {
                when (it) {
                    leftView -> {
                        selectItem(0)
                    }
                    centerView -> {
                        selectItem(1)
                    }
                    rightView -> {
                        when (style) {
                            Style.ONE -> {
                                selectItem(0)
                            }
                            Style.DOUBLE -> {
                                selectItem(1)
                            }
                            Style.TRIPLE -> {
                                selectItem(2)
                            }
                        }
                    }
                }
            }
            addView(this)
        }
    }

    private fun normal(view: DrawableTextView?) {
        view?.apply {
            background = null
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            val category = tag as Category
            setDrawable(DrawableTextView.LEFT, getDrawable(category.icon), mLabelWidth, mLabelHeight)
        }
    }

    private fun select(view: DrawableTextView?, @DrawableRes resId: Int) {
        normal(currentView)
        view?.apply {
            currentView = this
            setBackgroundResource(resId)
            typeface = Typeface.DEFAULT_BOLD
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mHighlightTextSize)
            setTextColor(getColor(R.color.color_20a0da))
            (tag as? Category)?.apply {
                setDrawable(DrawableTextView.LEFT, getDrawable(highlightIcon), mLabelWidth, mLabelHeight)
            }
        }
    }

    private fun select(view: DrawableTextView?, drawable: Drawable) {
        normal(currentView)
        view?.apply {
            currentView = this
            background = drawable
            typeface = Typeface.DEFAULT_BOLD
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mHighlightTextSize)
            setTextColor(getColor(R.color.color_20a0da))
            (tag as? Category)?.apply {
                setDrawable(DrawableTextView.LEFT, getDrawable(highlightIcon), mLabelWidth, mLabelHeight)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = measuredWidth
        mItemWidth = mWidth / style.count
        updateLayout(leftView)
        updateLayout(centerView)
        updateLayout(rightView)
    }

    private fun updateLayout(view: View?) {
        view?.apply {
            val params = layoutParams as MarginLayoutParams
            params.width = mItemWidth
            layoutParams = params
        }
    }

    private fun notifyChange() {
        when (style) {
            Style.ONE -> {
                centerView?.gone()
                rightView?.gone()
            }
            Style.DOUBLE -> {
                centerView?.gone()
                rightView?.visible()
            }
            Style.TRIPLE -> {
                centerView?.visible()
                rightView?.visible()
            }
        }
        requestLayout()
//        selectItem(0)
    }

    private fun bindItem(view: DrawableTextView?, category: Category) {
        view?.apply {
            tag = category
            text = category.label
            setDrawable(DrawableTextView.LEFT, getDrawable(category.icon), mLabelWidth, mLabelHeight)
        }
    }

    enum class Style(val count: Int) {

        /**
         * 一重导航
         */
        ONE(1),

        /**
         * 双重导航
         */
        DOUBLE(2),

        /**
         * 三重导航
         */
        TRIPLE(3)
    }

    enum class Category(
            val label: String,
            @DrawableRes val icon: Int,
            @DrawableRes val highlightIcon: Int,
    ) {
        CARD("卡片", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        TA_CARD("TA的卡片", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        CARD_C("简装", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        CARD_S("限量", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        COFFER("保险箱", R.drawable.ic_label_coffer, R.drawable.ic_label_coffer_highlight),
        SUIT("普卡套装", R.drawable.ic_label_suit, R.drawable.ic_label_suit_highlight),
        LIMIT("限量套装", R.drawable.ic_label_suit, R.drawable.ic_label_suit_highlight),
        MOST_SPACE("最多空位", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        MOST_COIN("最多钱", R.drawable.ic_label_coin, R.drawable.ic_label_coin),
        MOST_SUIT("最多套装", R.drawable.ic_label_suit, R.drawable.ic_label_suit_highlight),
        SHOPPING("购买", R.drawable.ic_label_shopping, R.drawable.ic_label_shopping_highlight),
        AUCTION("拍卖", R.drawable.ic_label_auction, R.drawable.ic_label_auction_highlight),
        BIDDING("竞价", R.drawable.ic_label_bidding, R.drawable.ic_label_bidding_highlight),
        TREASURE("宝箱", R.drawable.ic_label_suit, R.drawable.ic_label_suit_highlight),
        PROP_CARD("道具卡", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        WISHING("许愿墙", R.drawable.ic_label_wishwall, R.drawable.ic_label_wishwall_highlight),
        TA_WISHING("TA的愿望", R.drawable.ic_label_wishing, R.drawable.ic_label_wishing_highlight),
        MY_WISHING("我的愿望", R.drawable.ic_label_wishing, R.drawable.ic_label_wishing_highlight),
        MSG_BOARD("留言板", R.drawable.ic_label_msg_board, R.drawable.ic_label_msg_board_highlight),
        DEAL_INFO("交易信息", R.drawable.ic_label_deal_info, R.drawable.ic_label_deal_info_highlight),
        PROP_CARD_INFO("道具卡信息", R.drawable.ic_label_card, R.drawable.ic_label_card_highlight),
        CARD_FRIEND("卡友信息", R.drawable.ic_label_card_friend, R.drawable.ic_label_card_friend_highlight),
    }

}