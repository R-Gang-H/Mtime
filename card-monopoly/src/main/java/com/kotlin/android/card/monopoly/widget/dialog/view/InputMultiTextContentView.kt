package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 输入框、多文本对话框内容视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class InputMultiTextContentView : LinearLayout {

    private val mInputHeight = 38.dp
    private val mLabelPaddingTop = 10.dp
    private val mLabelPaddingBottom = 15.dp
    private val mTitleTextSize = 15F

    private val mLabelView1 by lazy { initLabelView() }
    private val mLabelView2 by lazy { initLabelView() }
    private val mInputView by lazy { initInputView() }

    var action: (() -> Unit)? = null

    var unit: String = ""
        set(value) {
            field = value
            mInputView.unit = value
        }

    var text: String? = ""
        get() = mInputView.text
        set(value) {
            field = value
            mInputView.text = value ?: ""
        }

    val number: Long
        get() {
            val price = mInputView.text
            return if (price.isNotEmpty()) {
                price.toLong()
            } else {
                0L
            }
        }

    var label1: CharSequence? = ""
        get() = mLabelView1.text.toString()
        set(value) {
            field = value
            mLabelView1.text = value ?: ""
        }

    var label2: CharSequence? = ""
        get() = mLabelView2.text.toString()
        set(value) {
            field = value
            mLabelView2.text = value ?: ""
        }

    var hint: String = ""
        get() = mInputView.hint
        set(value) {
            field = value
            mInputView.hint = value
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

        addView(mLabelView1)
        addView(mInputView)
        addView(mLabelView2)
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

    /**
     * 初始化输入框
     */
    private fun initInputView(): UnitInputView {
        return UnitInputView(context).apply {
            maxLength = 11
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)
            setBackground(
                    colorRes = R.color.color_ebedf2,
                    cornerRadius = 19.dpF
            )
            inputType = EditorInfo.TYPE_CLASS_NUMBER
        }
    }
}
