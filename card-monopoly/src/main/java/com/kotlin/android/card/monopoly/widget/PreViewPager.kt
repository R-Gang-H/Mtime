package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class PreViewPager : ViewPager {

    private var noScroll = false

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {

    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return !noScroll && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent?): Boolean {
        return try {
            !noScroll && super.onInterceptTouchEvent(arg0)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}