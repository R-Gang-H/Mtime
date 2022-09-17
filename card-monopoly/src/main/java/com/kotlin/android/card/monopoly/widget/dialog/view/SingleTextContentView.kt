package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 单一文本对话框内容视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class SingleTextContentView : LinearLayout {

    private val mLabelPaddingTop = 20.dp
    private val mLabelPaddingBottom = 20.dp
    private val mTitleTextSize = 15F

    private val mLabelView by lazy { initLabelView() }

    var action: (() -> Unit)? = null

    var message: CharSequence? = ""
        get() = mLabelView.text.toString()
        set(value) {
            field = value
            mLabelView.text = value ?: ""
        }

    var messageGravity: Int = Gravity.START
        set(value) {
            field = value
            (mLabelView.layoutParams as? LayoutParams)?.apply {
                gravity = value
                mLabelView.layoutParams = this
            }
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

        addView(mLabelView)
    }

    /**
     * 初始化文本框
     */
    private fun initLabelView(paddingTop: Int = mLabelPaddingTop, paddingBottom: Int = mLabelPaddingBottom): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, paddingTop, 0, paddingBottom)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
            setTextColor(getColor(R.color.color_8798af))
        }
    }
}
