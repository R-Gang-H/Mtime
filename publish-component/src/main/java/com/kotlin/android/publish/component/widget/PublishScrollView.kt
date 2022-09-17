package com.kotlin.android.publish.component.widget

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent
import android.widget.ScrollView
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i

/**
 * 发布富文本 ScrollView
 *
 * Created on 2020/7/14.
 *
 * @author o.s
 */
class PublishScrollView : ScrollView {

    private val tag = "1 ->"

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

    }

//    override fun dispatchDragEvent(event: DragEvent?): Boolean {
//        "$tag dispatchDragEvent".i()
//        return super.dispatchDragEvent(event)
//    }
//
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        "$tag dispatchTouchEvent".i()
//        return super.dispatchTouchEvent(ev)
//    }
//
//    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//        "$tag requestDisallowInterceptTouchEvent".i()
//        super.requestDisallowInterceptTouchEvent(disallowIntercept)
//    }
//
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        "$tag onInterceptTouchEvent".i()
//        return super.onInterceptTouchEvent(ev)
//    }
//
//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        super.onTouchEvent(ev)
//        ev?.action?.let {
//            when (it) {
//                MotionEvent.ACTION_DOWN -> {
//                    "$tag onTouchEvent ACTION_DOWN".e()
//                    downY = ev.y
//                }
//                MotionEvent.ACTION_UP -> {
//                    "$tag onTouchEvent ACTION_UP".e()
//                    handleUp(ev)
//                }
//                MotionEvent.ACTION_CANCEL -> {
//                    "$tag onTouchEvent ACTION_CANCEL".e()
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    "$tag onTouchEvent ACTION_MOVE".i()
//                }
//            }
//        }
//        return true
//    }
//
//    private var downY = 0F
//
//    private fun handleUp(ev: MotionEvent) {
//        val upY = ev.y
//    }
}