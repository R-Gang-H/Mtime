package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 链接文本对话框内容视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class LinkTextContentView : LinearLayout {

    private val mLabelPaddingTop = 15.dp
    private val mLabelPaddingBottom = 20.dp
    private val mLinkMarginBottom = 20.dp
    private val mTitleTextSize = 15F

    private val mLabelView by lazy { initLabelView() }
    private val mGoTaPocketView by lazy {
        initLinkView(R.string.continue_ta_pocket).apply {
            setOnClickListener {
                action?.invoke(ActionEvent.TA_POCKET)
            }
        }
    }
    private val mBackPocketView by lazy {
        initLinkView(R.string.back_my_pocket).apply {
            setOnClickListener {
                action?.invoke(ActionEvent.MY_POCKET)
            }
        }
    }

    var action: ((event: ActionEvent) -> Unit)? = null

    var message: CharSequence? = ""
        get() = mLabelView.text.toString()
        set(value) {
            field = value
            mLabelView.text = value ?: ""
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
        addView(mGoTaPocketView)
        addView(mBackPocketView)
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

    private fun initLinkView(@StringRes resId: Int): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = mLinkMarginBottom
            }
            setTextColor(getColor(R.color.color_1fcaea))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
            setText(resId)
        }
    }

    enum class ActionEvent {
        /**
         * 回到我的口袋
         */
        MY_POCKET,

        /**
         * 查看TA的口袋
         */
        TA_POCKET,
    }
}
