package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * 带单位的输入框视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class UnitInputView : FrameLayout {

    private val mInputHeight = 38.dp
    private val mInputPaddingStart = 15.dp
    private val mInputPaddingEnd = 30.dp
    private val mUnitMargin = 15.dp
    private val mInputTextSize = 14F
    private val mUnitTextSize = 16F

    private val mInputView by lazy { initInputView() }
    private val mUnitView by lazy { initUnitView() }

    var inputChange: ((len: Int) -> Unit)? = null
        set(value) {
            field = value
            mInputView.addTextChangedListener(mTextWatch)
        }

    var textChange: ((s: Editable) -> Unit)? = null
        set(value) {
            field = value
            mInputView.addTextChangedListener(mTextWatch)
        }

    var unit: String = ""
        set(value) {
            field = value
            mUnitView.text = value
            if (TextUtils.isEmpty(value)) {
                mInputView.setPadding(mInputPaddingStart, 0, mInputPaddingStart, 0)
            } else {
                mInputView.setPadding(mInputPaddingStart, 0, mInputPaddingEnd, 0)
            }
        }

    var text: String = "0"
        get() = mInputView.text.toString()
        set(value) {
            field = value
            mInputView.setText(value)
        }

    var hint: String = ""
        get() = mInputView.hint.toString()
        set(value) {
            field = value
            mInputView.hint = value
        }

    var maxLength: Int = 100
        set(value) {
            field = value
            mInputView.filters = arrayOf<InputFilter>(LengthFilter(value))
        }

    var inputType: Int = EditorInfo.TYPE_CLASS_TEXT
        set(value) {
            field = value
            mInputView.inputType = value
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
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)

        addView(mInputView)
        addView(mUnitView)
    }

    /**
     * 初始化输入框
     */
    private fun initInputView(): EditText {
        return EditText(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mInputHeight)
            setPadding(mInputPaddingStart, 0, mInputPaddingStart, 0)
            gravity = Gravity.CENTER_VERTICAL
            setBackground()
            setSingleLine()
            filters = arrayOf<InputFilter>(LengthFilter(maxLength))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mInputTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            setHintTextColor(getColor(R.color.color_8798af))
            inputChange?.apply {
                addTextChangedListener(mTextWatch)
            }
        }
    }
    /**
     * 初始化单位视图
     */
    private fun initUnitView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mInputHeight).apply {
                gravity = Gravity.END
                marginEnd = mUnitMargin
            }
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mUnitTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
        }
    }

    private val mTextWatch by lazy {
        object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.apply {
                    inputChange?.invoke(s.length)
                    textChange?.invoke(s)
                }
            }

        }
    }
}