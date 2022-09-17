package com.kotlin.android.card.monopoly.widget.search

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView

/**
 * 高亮文本视图：
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */
class HighlightTextView : AppCompatTextView {

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

    fun setText(text: String, highlightText: String, @ColorRes colorRes: Int) {

    }

    fun setText(text: String, highlightText: ArrayList<String>, @ColorRes colorRes: Int) {

    }
}