package com.kotlin.android.widget.marquee

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 跑马灯视图:
 *
 * Created on 2020/11/12.
 *
 * @author o.s
 */
class MarqueeTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun isFocused(): Boolean {
        return true
    }

    private fun initView() {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        isSingleLine = true
        isSelected = true
        marqueeRepeatLimit = -1
    }
}