package com.kotlin.android.card.monopoly.widget.card.item

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.CardImageView
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 可选择的卡片视图：
 *
 * Created on 2020/11/13.
 *
 * @author o.s
 */
class SelectCardItemView : FrameLayout {

//    private val mPadding: Int = 2.dp

    private val mCheckWidth: Int = 26.dp
    private val mCheckHeight: Int = mCheckWidth

    private val mCardView by lazy {
        CardImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            setPadding(mPadding, mPadding, mPadding, mPadding)
//            background = getStateListDrawable(
//                    normal = getDrawable(R.drawable.transparent),
//                    pressed = getShapeDrawable(
//                            colorRes = R.color.color_feb12a,
//                            cornerRadius = 3
//                    ),
//                    selected = getShapeDrawable(
//                            colorRes = R.color.color_feb12a,
//                            cornerRadius = 3
//                    )
//            )
            foreground = getDrawableStateList(
                    normal = getShapeDrawable(colorRes = R.color.color_00000000, cornerRadius = 3.dpF),
                    selected = getShapeDrawable(
                            colorRes = R.color.color_33feb12a,
                            strokeColorRes = R.color.color_feb12a,
                            strokeWidth = 1.dp,
                            cornerRadius = 2.dpF
                    ),
                    disable = getShapeDrawable(colorRes = R.color.color_00000000, cornerRadius = 3.dpF)
            )
            stateChange = {
                this@SelectCardItemView.state = it
            }
//            setOnClickListener {
//                action?.invoke(card)
//            }
        }
    }

    private val mCheckView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(mCheckWidth, mCheckHeight).apply {
                gravity = Gravity.CENTER
            }
            setImageDrawable(getDrawableStateList(
                    normal = getShapeDrawable(),
                    selected = getDrawable(R.drawable.ic_select_card),
                    disable = getDrawable(R.drawable.ic_lock_card)
            ))
        }
    }

//    var action: ((card: Card?) -> Unit)? = null

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
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(mCardView)
        addView(mCheckView)
    }

    private fun fillCardView() {
        mCardView.card = card
    }

    private fun notifyChange() {
        when (state) {
            CardState.SELECTED -> {
                mCardView.isSelected = true
                mCheckView.apply {
                    isEnabled = true
                    isSelected = true
                }
            }
            else -> {
                mCardView.isSelected = false
                mCheckView.apply {
                    isEnabled = true
                    isSelected = false
                }
            }
        }
    }
}