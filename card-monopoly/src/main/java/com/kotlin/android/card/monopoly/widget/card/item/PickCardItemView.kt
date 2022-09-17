package com.kotlin.android.card.monopoly.widget.card.item

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.CardImageView
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 拾取的卡片视图：
 *
 * Created on 2020/11/13.
 *
 * @author o.s
 */
class PickCardItemView : LinearLayout {

    private val mPadding: Int = 3.dp
    private val mPaddingTop: Int = 9.dp
    private val mActionHeight: Int = 22.dp
    private val mActionPadding: Int = 15.dp
    private val mActionMargin: Int = 5.dp
    private val mTextSize = 12F // sp

    private val mCardView by lazy {
        CardImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(mPadding, mPaddingTop, mPadding, mPaddingTop)
            background = getDrawableStateList(
                    normal = getShapeDrawable(colorRes = R.color.color_d8f4ff, cornerRadius = 3.dpF),
                    selected = getShapeDrawable(colorRes = R.color.color_feb12a, cornerRadius = 3.dpF),
                    disable = getShapeDrawable(colorRes = R.color.color_d8f4ff, cornerRadius = 3.dpF)
            )
            stateChange = {
                this@PickCardItemView.state = it
            }
        }
    }

    private val mActionView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = mActionMargin
                bottomMargin = mActionMargin
            }
            gravity = Gravity.CENTER
            setPadding(mActionPadding, 0, mActionPadding, 0)
            background = getDrawableStateList(
                    normal = getShapeDrawable(colorRes = R.color.color_12c7e9, cornerRadius = 11.dpF),
                    selected = getShapeDrawable(colorRes = R.color.color_12c7e9, cornerRadius = 11.dpF),
                    disable = getShapeDrawable(colorRes = R.color.color_6612c7e9, cornerRadius = 11.dpF)
            )
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.pickup)
            setOnClickListener {
                action?.invoke(card)
            }
        }
    }

    var action: ((card: Card?) -> Unit)? = null

    var card: Card? = null
        set(value) {
            field = value
            fillCardView()
        }

    var state: CardState = CardState.EMPTY
        set(value) {
            field = value
            mCardView.state = value
            notifyChange()
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
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(mCardView)
        addView(mActionView)
    }

    private fun fillCardView() {
        mCardView.card = card
    }

    private fun notifyChange() {
        when (state) {
            CardState.SELECTED -> {
                selectedState()
            }
            CardState.FILL -> {
                fillState()
            }
            CardState.EMPTY -> {
                emptyState()
            }
            CardState.LOCK -> {
                clockState()
            }
            CardState.NO_DISPLAY -> {
                noDisplayState()
            }
        }
    }

    private fun selectedState() {
        mCardView.apply {
            isEnabled = true
            isSelected = true
        }
        mActionView.isEnabled = true
    }

    private fun fillState() {
        mCardView.apply {
            isEnabled = true
            isSelected = false
        }
        mActionView.isEnabled = true
    }

    private fun emptyState() {
        mCardView.apply {
            isEnabled = true
            isSelected = false
        }
        mActionView.isEnabled = false
    }

    private fun clockState() {
        mCardView.apply {
            isEnabled = false
            isSelected = false
        }
        mActionView.isEnabled = false
    }

    private fun noDisplayState() {
        mCardView.isSelected = false
        mActionView.isEnabled = false
    }

}