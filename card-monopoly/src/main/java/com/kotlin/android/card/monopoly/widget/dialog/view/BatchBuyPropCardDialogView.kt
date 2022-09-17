package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan

/**
 * @desc  批量购买宝箱view
 * @author zhangjian
 * @date 2021-05-27 11:15:24
 */
class BatchBuyPropCardDialogView : LinearLayout {
    //总价
    var mPrice: Long = 0L
    private val mInputHeight = 38.dp
    private val mTextTipHeight = 26.dp
    private val totalHeight = 90.dp

    private val mTopTv by lazy { initTopLabel() }
    private val mPriceTv by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTextTipHeight)
            gravity = Gravity.CENTER
        }
    }

    //输入框
    private val mInputView by lazy {
        UnitInputView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)
            setBackground(
                colorRes = R.color.color_ebedf2,
                cornerRadius = 19.dpF
            )
            inputType = EditorInfo.TYPE_CLASS_NUMBER
            unit = getString(R.string.zhang)
            hint = "请输入数量"
            maxLength = 10
            textChange = {
                if (it.isNotEmpty()) {
                    setTotalPrice(it.toString().toLong())
                } else {
                    setTotalPrice(0L)
                }
            }
        }
    }

    //文本框中的内容
    var text: String? = ""
        get() = mInputView.text
        set(value) {
            field = value
            mInputView.text = value ?: ""
        }

    //合计总价展示
    var label1: CharSequence? = ""
        get() = mPriceTv.text.toString()
        set(value) {
            field = value
            mPriceTv.text = value ?: ""
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

    private fun initView() {
        orientation = VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, totalHeight)
        addView(initTopLabel())
        addView(mInputView)
        addView(mPriceTv)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }


    /**
     * 计算总价
     */
    private fun setTotalPrice(toLong: Long) {
        label1 = if (toLong != 0L) {
            val total = (toLong * mPrice).toString()
            "合计: ".toSpan().append(total.toSpan().toColor(color = getColor(R.color.color_ff5a36)))
                .append("金币")
        } else {
            ""
        }
    }

    /**
     * 标题
     */
    private fun initTopLabel(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mTextTipHeight)
            gravity = Gravity.CENTER
            text = "请输入你需要购买的数量"
        }
    }
}