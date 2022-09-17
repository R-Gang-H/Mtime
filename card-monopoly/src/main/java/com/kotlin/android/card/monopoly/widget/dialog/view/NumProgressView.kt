package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView

/**
 * 数字进度条视图：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class NumProgressView : AppCompatTextView {

    /**
     * 进度最大值
     */
    var limit: Int = 100
        set(value) {
            field = value
            text = "$progress/$value"
        }

    /**
     * 进度数
     */
    var progress: Int = 0
        set(value) {
            field = value
            text = "$value/$limit"
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

    }
}