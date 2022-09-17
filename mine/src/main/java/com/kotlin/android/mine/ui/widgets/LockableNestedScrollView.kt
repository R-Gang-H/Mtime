package com.kotlin.android.mine.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

/**
 * create by lushan on 2020/9/9
 * description: 给NestedScrollView添加滑动开关
 */
class LockableNestedScrollView @JvmOverloads constructor(var ctx:Context,var attrs:AttributeSet? = null,var defaultStyle:Int = -1):NestedScrollView(ctx,attrs,defaultStyle) {

    private var canScroll = true
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onTouchEvent(ev)
    }

    fun setCanScroll(canScroll:Boolean){
        this.canScroll = canScroll
    }

}