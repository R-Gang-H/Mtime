package com.kotlin.android.card.monopoly.widget.nested

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 嵌套防滑RecyclerView
 *
 * Created on 2020/8/31.
 *
 * @author o.s
 */
class NestedRecyclerView : RecyclerView {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {

    }

    private var downY = 0F
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        event?.action?.let {
            when (it) {
                MotionEvent.ACTION_DOWN -> {
                    downY = event.y
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (downY == 0F) {
                        downY = event.y
                    }
                    val offsetY = event.y - downY
                    if (abs(offsetY) > 200) {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }
        return true
    }
}