package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants.AUCTION_TIME_EIGHT
import com.kotlin.android.card.monopoly.constants.Constants.AUCTION_TIME_SIXTEEN
import com.kotlin.android.card.monopoly.constants.Constants.AUCTION_TIME_TWO
import com.kotlin.android.card.monopoly.constants.Constants.KEY_FIX_PRICE
import com.kotlin.android.card.monopoly.constants.Constants.KEY_START_PRICE
import com.kotlin.android.card.monopoly.constants.Constants.KEY_TIME_AUCTION
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_CARD
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_SUIT
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_TOOLS
import com.kotlin.android.card.monopoly.widget.AuctionCardFilterView
import com.kotlin.android.card.monopoly.widget.search.SelectCardView
import com.kotlin.android.card.monopoly.widget.search.SelectSuitView
import com.kotlin.android.core.CoreApp
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.ktx.ext.store.getSpValue
import kotlin.math.ceil

/**
 * 拍卖对话框内容视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class AuctionContentView : FrameLayout {

    private val mHeight = 190.dp
    private val mRightHeight = 190.dp
    private val mRightCenterHeight = 88.dp
    private val mInputHeight = 38.dp
    private val mFeeHeight = 26.dp
    private val mIconWidth = 66.dp
    private val mIconHeight = 97.dp
    private val mTimeHeight = 38.dp

    private val mRightMarginStart = 80.dp
    private val mPaddingBottom = 15.dp
    private val mTimePaddingTop = 25.dp
    private val mTextSize = 14F

    private val mRightView by lazy { initRightLayout() }
    private val mLeftView by lazy { initLeftLinearLayout() }
    private val mRightCenterView by lazy { initRightCenterLayout() }
    private val mIconView by lazy { initIconView() }
    private val mSuitView by lazy { initSuitView() }
    private val mTwoHourView by lazy { initTimeView(getString(R.string.two_hour), 0, 6.dp) }
    private val mEightHourView by lazy { initTimeView(getString(R.string.eight_hour), 3.dp, 3.dp) }
    private val mSixteenHourView by lazy { initTimeView(getString(R.string.sixteen_hour), 6.dp, 0) }
    private val mFloorPriceView by lazy {
        initInputView().apply {
            hint = getString(R.string.floor_price)
            maxLength = 11
            textChange = {
                if (it.isNotEmpty()) {
                    setupFee(it.toString().toLong())
                }
            }
            if (CoreApp.instance.getSpValue(KEY_START_PRICE, "").isNotEmpty()) {
                text = CoreApp.instance.getSpValue(KEY_START_PRICE, "").toString()
            }
        }
    }
    private val mPriceView by lazy {
        initInputView().apply {
            hint = getString(R.string.price_)
            maxLength = 11
            if (CoreApp.instance.getSpValue(KEY_FIX_PRICE, "").isNotEmpty()) {
                text = CoreApp.instance.getSpValue(KEY_FIX_PRICE, "")
            }
        }
    }
    private val mFeeView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mFeeHeight)
            gravity = Gravity.CENTER_VERTICAL
//            setText(R.string.auction_fee)
            setTextColor(getColor(R.color.color_8798af))
        }
    }

    private val mTypeFilterView by lazy {
        initCardFilterView()
    }


    var action: ((type: Long) -> Unit)? = null

    /**
     * 卡片
     */
    var card: Card? = null
        set(value) {
            field = value
            mIconView.card = value
        }

    /**
     * 道具卡
     */
    var propCard: PropCard? = null
        set(value) {
            field = value
            if (value != null) {
                mIconView.card = Card(cardCover = value.toolCover)
            } else {
                mIconView.card = null
            }
        }

    /**
     * 套装
     */
    var suit: Suit? = null
        set(value) {
            field = value
            mSuitView.suit = value
        }

    /**
     * 卡片Id 【cardId与toolCardId互斥，只能一个有值】
     */
    val cardId: Long?
        get() {
            return card?.userCardId
        }

    /**
     * 底价
     */
    val startPrice: Long
        get() {
            val price = mFloorPriceView.text
            return if (price.isNotEmpty()) {
                price.toLong()
            } else {
                0L
            }
        }

    /**
     * 一口价
     */
    val fixPrice: Long
        get() {
            val price = mPriceView.text
            return if (price.isNotEmpty()) {
                price.toLong()
            } else {
                0L
            }
        }

    /**
     * 拍卖时限
     */
    val timeLimited: Long
        get() {
            return when {
                mEightHourView.isSelected -> {
                    AUCTION_TIME_EIGHT
                }
                mSixteenHourView.isSelected -> {
                    AUCTION_TIME_SIXTEEN
                }
                else -> {
                    AUCTION_TIME_TWO
                }
            }
        }

    /**
     * 保管费系数
     */
    private val feeRatio: Float
        get() {
            return when {
                mEightHourView.isSelected -> {
                    0.1F
                }
                mSixteenHourView.isSelected -> {
                    0.2F
                }
                else -> {
                    0.05F
                }
            }
        }

    private fun setupFee(floorPrice: Long) {
        var fee = ceil(floorPrice * feeRatio).toLong()
        if (fee < 10L) {
            fee = 10L
        }
        mFeeView.text = "保管费".toSpan()
            .append(
                fee.toString().toSpan().toColor(color = getColor(R.color.color_feb12a))
            ).append(
                "G"
            )
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
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
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight)
//        setPadding(0, 0, 0, mPaddingBottom)

        mRightCenterView.addView(mTwoHourView)
        mRightCenterView.addView(mEightHourView)
        mRightCenterView.addView(mSixteenHourView)

        mRightView.addView(mFloorPriceView)
        mRightView.addView(mRightCenterView)
        mRightView.addView(mPriceView)
        mRightView.addView(mFeeView)

        mLeftView.addView(mTypeFilterView)
        mLeftView.addView(mIconView)
        mLeftView.addView(mSuitView)


        addView(mLeftView)
        addView(mRightView)

        if (CoreApp.instance.getSpValue(KEY_TIME_AUCTION, 0L) != 0L) {
            when (CoreApp.instance.getSpValue(KEY_TIME_AUCTION, 0L)) {
                AUCTION_TIME_TWO -> {
                    setSelectState(two = true)
                }
                AUCTION_TIME_EIGHT -> {
                    setSelectState(eight = true)
                }
                AUCTION_TIME_SIXTEEN -> {
                    setSelectState(sixTeen = true)
                }
            }
        } else {
            setSelectState(two = true)
        }
    }

    private fun initRightLayout(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mRightHeight).apply {
                marginStart = mRightMarginStart
            }
        }
    }

    private fun initLeftLinearLayout(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(mIconWidth, mRightHeight)
        }
    }

    private fun initRightCenterLayout(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mRightCenterHeight).apply {
            }
            setPadding(0, mTimePaddingTop, 0, mTimePaddingTop)
        }
    }

    private fun initIconView(): SelectCardView {
        return SelectCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(mIconWidth, mIconHeight).apply {
                this.topMargin = 10.dp
            }
            hint = getString(R.string.chose_card)
            action = {
                this@AuctionContentView.action?.invoke(
                    mTypeFilterView.type ?: TYPE_CARD
                )
            }
        }
    }

    private fun initSuitView(): SelectSuitView {
        return SelectSuitView(context).apply {
            layoutParams = LinearLayout.LayoutParams(mIconWidth, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                this.topMargin = 10.dp
            }
            isVisible = false
            hint = getString(R.string.chose_suit)
            action = {
                this@AuctionContentView.action?.invoke(
                    mTypeFilterView.type ?: TYPE_CARD
                )
            }
        }
    }

    private fun initTimeView(time: String, start: Int = 0, end: Int = 0): TextView {
        return TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, mTimeHeight).apply {
                weight = 1F
                marginStart = start
                marginEnd = end
            }
            gravity = Gravity.CENTER
            background = getDrawableStateList(
                normal = getShapeDrawable(
                    strokeColorRes = R.color.color_8798af,
                    strokeWidth = 1.dp,
                    cornerRadius = 19.dpF
                ),
                pressed = getShapeDrawable(
                    strokeColorRes = R.color.color_feb12a,
                    strokeWidth = 1.dp,
                    cornerRadius = 19.dpF
                ),
                selected = getShapeDrawable(
                    strokeColorRes = R.color.color_feb12a,
                    strokeWidth = 1.dp,
                    cornerRadius = 19.dpF
                )
            )
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(
                getColorStateList(
                    normalColor = getColor(R.color.color_4e5e73),
                    pressColor = getColor(R.color.color_feb12a),
                    selectedColor = getColor(R.color.color_feb12a)
                )
            )
            text = time

            setOnClickListener {
                syncSelected(it)
                it.isSelected = true
                val fee = mFloorPriceView.text
                if (TextUtils.isEmpty(fee)) {
                    mFeeView.text = ""
                } else {
                    setupFee(fee.toLong())
                }
            }
        }
    }

    private fun initCardFilterView(): AuctionCardFilterView {
        return AuctionCardFilterView(context).apply {
            setImgShowType(TYPE_TOOLS, false)
            setState(TYPE_CARD)
            action = {
                when (it) {
                    TYPE_CARD -> {
                        mIconView.hint = getString(R.string.chose_card)
                        mIconView.isVisible = true
                        mSuitView.isVisible = false
                        clearData()
                    }
                    TYPE_SUIT -> {
                        mIconView.hint = getString(R.string.chose_suit)
                        mIconView.isVisible = false
                        mSuitView.isVisible = true
                        mSuitView.state = SelectSuitView.State.SELECT
                        clearData()
                    }
                }
            }
        }
    }

    /**
     * 切换标签展示
     */
    private fun clearData(){
        card = null
        propCard = null
        suit = null
    }

    private fun syncSelected(view: View) {
        if (mTwoHourView != view) {
            mTwoHourView.isSelected = false
        }
        if (mEightHourView != view) {
            mEightHourView.isSelected = false
        }
        if (mSixteenHourView != view) {
            mSixteenHourView.isSelected = false
        }
    }

    private fun setSelectState(
        two: Boolean? = false,
        eight: Boolean? = false,
        sixTeen: Boolean? = false
    ) {
        mTwoHourView.isSelected = two == true
        mEightHourView.isSelected = eight == true
        mSixteenHourView.isSelected = sixTeen == true
    }

    /**
     * 初始化输入框
     */
    private fun initInputView(): UnitInputView {
        return UnitInputView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)
            setBackground(
                colorRes = R.color.color_ebedf2,
                cornerRadius = 19.dpF
            )
            inputType = EditorInfo.TYPE_CLASS_NUMBER
            unit = getString(R.string.g)
        }
    }
}
