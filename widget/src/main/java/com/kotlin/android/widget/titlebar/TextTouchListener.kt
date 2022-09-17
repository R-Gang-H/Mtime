package com.kotlin.android.widget.titlebar

import android.content.Context
import android.text.TextUtils
import android.util.Range
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

/**
 * 通用的文本触摸点击事件拦截：
 * 包含点击的Left、Right Drawable
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
class TextTouchListener(
        context: Context,
        val textView: TextView,
        val link: String? = null,
        val action: (data: Data) -> Unit
) : View.OnTouchListener {

    init {
        // 必须可点击，否则 touch 事件回调不正常
        textView.isClickable = true
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (endGestureDetector.onTouchEvent(event)) {
            action.invoke(Data(Position.END))
        }
        if (startGestureDetector.onTouchEvent(event)) {
            action.invoke(Data(Position.START))
        }
        if (linkGestureDetector.onTouchEvent(event)) {
            action.invoke(Data(Position.CENTER, link))
        }
        return false
    }

    private val endGestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (!isEndClick()) {
                    return false
                }
                e?.apply {
                    textView.apply {
                        val endX = width - paddingEnd
                        val startX = endX - height
                        if (e.x > startX && e.x < endX) {
                            return true
                        }
                    }
                }
                return false
            }

        })
    }

    private val startGestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (!isStartClick()) {
                    return false
                }
                e?.apply {
                    textView.apply {
                        val startX = paddingStart - height
                        val endX = startX + height
                        if (e.x > startX && e.x < endX) {
                            return true
                        }
                    }
                }
                return false
            }

        })
    }

    private val linkGestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                if (TextUtils.isEmpty(link)) {
                    return false
                }
                e?.apply {
                    textView.apply {
                        getLinkRangeX()?.apply {
                            if (e.x > lower && e.x < upper) {
                                return true
                            }
                        }
                    }
                }
                return false
            }

        })
    }

    private fun isStartClick(): Boolean {
        return textView.compoundDrawables[0] != null
    }

    private fun isEndClick(): Boolean {
        return textView.compoundDrawables[2] != null
    }

    private fun getLinkRangeX(): Range<Float>? {
        if (link == null)
            return null
        val startIndex = textView.text.indexOf(link)
        if (startIndex < 0) {
            return null
        }
        val endIndex = startIndex + link.length
        return textView.layout?.run {
            val lineStart = getLineStart(0) // 单行文本
            val orgStart = getPrimaryHorizontal(lineStart)
            val start = getPrimaryHorizontal(startIndex) - orgStart
            val end = getSecondaryHorizontal(endIndex) - orgStart
            Range(start, end)
        }
    }

    data class Data(
            val position: Position,
            val link: String? = null
    )

    /**
     * 点击位置
     */
    enum class Position {

        /**
         * 开始（左边）位置
         */
        START,

        /**
         * 结束（右边）位置
         */
        END,

        /**
         * 超链接（中间）位置
         */
        CENTER
    }
}