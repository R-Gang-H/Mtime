package com.kotlin.android.widget.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.R
import com.kotlin.android.widget.titlebar.TextTouchListener

/**
 * 搜索输入框
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
class SearchEditText : AppCompatEditText {

    private val mPadding = 10.dp
//    private val mDrawablePadding = 2.dp
    private val mDrawableWidth = 30.dp
    private val mDrawableHeight = mDrawableWidth
    private var mTextSize = 14F

    private var startDrawable: Drawable? = null
    private var endDrawable: Drawable? = null

    /**
     * 键盘监听搜索事件回调
     */
    var searchAction: ((event: SearchEvent) -> Unit)? = null

    /**
     * 是否是自动搜索
     */
    var isAutoSearch = true

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    fun init() {
        setBackground(
                colorRes = R.color.color_f4f6f8,
                cornerRadius = 18.dpF
        )

        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
        isFocusable = true
        imeOptions = EditorInfo.IME_ACTION_SEARCH
//        compoundDrawablePadding = mDrawablePadding
        gravity = Gravity.CENTER_VERTICAL
        setPadding(mPadding, 0, mPadding, 0)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
        setTextColor(getColor(R.color.color_1d2736))
        setHintTextColor(getColor(R.color.color_cbd0d7))
        addTextChangedListener(watcher)
        setOnTouchListener(TextTouchListener(
                context = context,
                textView = this,
        ) {
            if (it.position == TextTouchListener.Position.END) {
                clearInput()
            }
        })
        setOnEditorActionListener { v, actionId, event ->
            if (/*v.text.isNotEmpty() &&*/ actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchAction?.invoke(SearchEvent(v.text.toString(), 1))
            }
            return@setOnEditorActionListener true
        }

        setStartIcon()
        setEndIcon()
    }

    /**
     * 设置开始位置的搜索图标（放大镜Icon）
     */
    fun setStartIcon(
            @DrawableRes res: Int = R.drawable.ic_title_bar_serach
    ) {
        startDrawable = getDrawable(res)?.apply {
            setBounds(0, 0, mDrawableWidth, mDrawableHeight)
            setCompoundDrawables(startDrawable, null, null, null)
        }
    }

    /**
     * 设置结束位置的搜索图标（清空Icon）
     */
    fun setEndIcon(
            @DrawableRes res: Int = R.drawable.ic_title_bar_center_end_close
    ) {
        endDrawable = getDrawable(res)?.apply {
            setBounds(0, 0, mDrawableWidth, mDrawableHeight)
        }
    }

    private fun resetDrawable() {
        if (compoundDrawables[2] != null) {
            setCompoundDrawables(compoundDrawables[0], null, null, null)
        }
    }

    private fun setEndDrawable() {
        if (compoundDrawables[2] == null) {
            setCompoundDrawables(compoundDrawables[0], null, endDrawable, null)
        }
    }

    private fun clearInput() {
        setText("")
        resetDrawable()
    }

    private val watcher by lazy {
        object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if(isAutoSearch){
                        searchAction?.invoke(SearchEvent(it.toString()))
                    }
                }
                if (s?.isNotEmpty() == true) {
                    setEndDrawable()
                } else {
                    resetDrawable()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        }
    }

    data class SearchEvent(
            var keyword: String = "",
            val event: Int = 0 // 0.默认； 1.点击搜索
    )
}