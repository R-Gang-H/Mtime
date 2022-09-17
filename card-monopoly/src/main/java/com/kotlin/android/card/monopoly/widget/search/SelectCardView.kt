package com.kotlin.android.card.monopoly.widget.search

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.CardImageView
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 选择卡片视图
 *
 * Created on 2020/9/16.
 *
 * @author o.s
 */
class SelectCardView : FrameLayout {

    private val mIconWidth = 67.dp
    private val mIconHeight = 98.dp
    private val mIconActionWidth = 34.dp
    private val mIconActionHeight = 34.dp
    private val mIconPaddingBottom4 = 20.dp
    private val mIconPaddingBottom = 10.dp
    private val mIconPadding = 10.dp
    private val mIconPadding4 = 5.dp

    private val mIconActionTextSize = 14F // sp

    private var mActionView: TextView? = null
    private var mCardView: CardImageView? = null

    var action: ((view: View) -> Unit)? = null

    var stateChange: ((state: CardState) -> Unit)? = null
        set(value) {
            field = value
            mCardView?.stateChange = value
        }

    var card: Card? = null
        set(value) {
            field = value
            mCardView?.card = value
            if (value == null) {
                mCardView?.state = CardState.NO_DISPLAY
            }
        }

    var hint: String = ""
        set(value) {
            field = value
            updatePadding(value)
            mActionView?.text = value
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
    }

    private fun initView() {
        layoutParams = LayoutParams(mIconWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

        mActionView = addIconActionView()
        mCardView = addIconView()
    }

    private fun addIconActionView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            getDrawable(R.drawable.ic_select_img)?.apply {
                setBounds(0, 0, mIconActionWidth, mIconActionHeight)
                setCompoundDrawables(null, this, null, null)
            }
            setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 6.dpF
            )
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPaddingBottom)
            compoundDrawablePadding = 6.dp
            setTextColor(getColor(R.color.color_12c7e9))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mIconActionTextSize)
            setText(R.string.find_the_card_you_want)
            setOnClickListener {
//                if (CardState.NO_DISPLAY == mCardView?.state) {
//                }
                action?.invoke(it)
            }

            addView(this)
        }
    }

    private fun addIconView(): CardImageView {
        return CardImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

            state = CardState.NO_DISPLAY

            this@SelectCardView.addView(this)
        }
    }

    private fun updatePadding(hint: String) {
        if (hint.length <= 4) {
            mActionView?.setPadding(mIconPadding4, mIconPadding, mIconPadding4, mIconPaddingBottom4)
        } else {
            mActionView?.setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPaddingBottom)
        }
    }
}