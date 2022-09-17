package com.kotlin.android.card.monopoly.widget.search

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.SuitImageView
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 选择套装视图
 *
 * Created on 2020/9/16.
 *
 * @author o.s
 */
class SelectSuitView : FrameLayout {

    private val mIconWidth = 67.dp
//    private val mIconHeight = 98.dp
    private val mIconActionWidth = 34.dp
    private val mIconActionHeight = 34.dp
    private val mIconPaddingBottom4 = 25.dp
    private val mIconPaddingBottom = 15.dp
    private val mIconPadding = 10.dp
    private val mIconPadding4 = 5.dp

    private val topDrawable by lazy {
        getDrawable(R.drawable.ic_select_img)?.apply {
            setBounds(0, 0, mIconActionWidth, mIconActionHeight)
        }
    }

    private val mIconActionTextSize = 14F // sp

    private var mActionView: TextView? = null
    private var mSuitView: SuitImageView? = null

    var action: ((view: View) -> Unit)? = null

    var state: State = State.SELECT
        set(value) {
            field = value
            notifyChangeState()
        }

    var suit: Suit? = null
        set(value) {
            field = value
            mSuitView?.data = suit
            if (!TextUtils.isEmpty(value?.suitCover)) {
                state = State.FULL
            }
        }

    var hint: String = ""
        set(value) {
            field = value
            updatePadding(value)
            mActionView?.text = value
        }

    var clickEnable: Boolean = false
        set(value) {
            field = value
            mActionView?.isEnabled = value
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
        mSuitView = addIconView()
    }

    private fun addIconActionView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setCompoundDrawables(null, topDrawable, null, null)
            setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 6.dpF
            )
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPaddingBottom)
            compoundDrawablePadding = 6.dp
            setTextColor(getColor(R.color.color_12c7e9))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mIconActionTextSize)
            setText(R.string.chose_suit)
            setOnClickListener {
                action?.invoke(it)
            }

            addView(this)
        }
    }

    private fun addIconView(): SuitImageView {
        return SuitImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

            this@SelectSuitView.addView(this)
        }
    }

    private fun updatePadding(hint: String) {
        if (hint.length <= 4) {
            mActionView?.setPadding(mIconPadding4, mIconPadding, mIconPadding4, mIconPaddingBottom4)
        } else {
            mActionView?.setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPaddingBottom)
        }
    }

    private fun notifyChangeState() {
        when (state) {
            State.SELECT -> {
                selectState()
            }
            State.FULL -> {
                fullState()
            }
            State.EMPTY -> {
                emptyState()
            }
        }
    }

    private fun selectState() {
        mSuitView?.data = null
        mActionView?.apply {
            setCompoundDrawables(null, topDrawable, null, null)
            setText(R.string.chose_suit)
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 6.dpF
            )
        }
    }

    private fun fullState() {
        mActionView?.apply {
            setCompoundDrawables(null, null, null, null)
            text = ""
            background = null
        }
    }

    private fun emptyState() {
        mSuitView?.data = null
        mActionView?.apply {
            setCompoundDrawables(null, null, null, null)
            text = ""
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 6.dpF
            )
        }
    }

    /**
     * 套装选择器状态
     */
    enum class State {
        SELECT, // 选择状态
        FULL, // 填充状态
        EMPTY, // 空状态
    }
}