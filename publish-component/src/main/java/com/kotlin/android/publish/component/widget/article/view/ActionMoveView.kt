package com.kotlin.android.publish.component.widget.article.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.kotlin.android.ktx.ext.core.scroll
import com.kotlin.android.publish.component.logD
import com.kotlin.android.publish.component.logE
import com.kotlin.android.publish.component.logW
import com.kotlin.android.publish.component.widget.ActionEvent

/**
 * 移动事件视图
 *
 * Created on 2020/7/14.
 *
 * @author o.s
 */
class ActionMoveView : androidx.appcompat.widget.AppCompatImageView {

    /**
     * 拖拽改变
     */
    var dragChange: ((v: EditorItemLayout, dy: Float) -> Unit)? = null

    private var itemView: EditorItemLayout? = null
    private var action: ((v: EditorItemLayout, actionEvent: ActionEvent) -> Unit)? = null

    fun setAction(
        itemView: EditorItemLayout?,
        action: ((v: EditorItemLayout, actionEvent: ActionEvent) -> Unit)?
    ) {
        this.itemView = itemView
        this.action = action
    }

    private var longClick = false

    private var rawY = 0F

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
        setupEvent()
    }

    private fun setupEvent() {
        setOnLongClickListener {
            itemView?.run {
                val oldH = height
                // 回调事件
                action?.invoke(this, ActionEvent.EVENT_MOVE)
//                state = EditorState.MOVE
//                val b = bottom
//                val t = b - moveStateHeight
//                layout(left, t, right, b)
//                logE("handLongClickEvent", "$b-$t=$measuredHeight")
                // 高亮itemView
//                highlightView()
                longClick = true
                logE("longClick", "$longClick")
//                post {
//                    val offsetHeight = height - oldH
//                    logE("longClick", "start move height($oldH -> $height), offsetHeight:$offsetHeight")
//                    // 回调事件
////                    action?.invoke(this, ActionEvent.EVENT_MOVE)
////                    // 高亮itemView
////                    highlightView()
//                    // 更改长按状态
//                    val layout = parent as EditorLayout
////                    val scrollView = layout.parent.parent as ScrollView
////                    layout.postDelayed({
////                        scrollView.scroll(offsetHeight)
////                    }, 2000)
//                    layout.postDelayed({
//                        longClick = true
//                        logE("longClick", "$longClick")
//                    }, 50)
//
//                    logE("longClick", "$longClick")
//                }
                true
            } ?: false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    rawY = it.y
                    logE("onTouchEvent", "ACTION_DOWN")
                }
                MotionEvent.ACTION_UP -> {
                    logE("onTouchEvent", "ACTION_UP")
                    longClickUp()
                }
                MotionEvent.ACTION_CANCEL -> {
                    logE("onTouchEvent", "ACTION_CANCEL")
                    resetCancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    handLongClickEvent(it)
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    /**
     * 处理长按事件
     */
    private fun handLongClickEvent(event: MotionEvent) {
        if (longClick) {
            if (rawY == 0F) {
                rawY = event.y
            }
            parent.requestDisallowInterceptTouchEvent(true)
            val y = event.y
            val dy = y - rawY
//            val itemView = parent.parent as PublishItemView
            itemView?.let {
                val layout = it.parent as EditorLayout
                val scrollView = layout.parent.parent as ScrollView
                val scrollHeight = scrollView.height
                val itemHeight = it.moveStateHeight
                var itemBottom = (it.bottom + dy).toInt()
                var itemTop = itemBottom - itemHeight

                val scrollY = scrollView.scrollY
                val maxHeight = layout.height // - layout.paddingTop - layout.paddingBottom
                val endScrollY = scrollY + scrollHeight - layout.paddingBottom
                val count = layout.childCount
                if (count > 0) {
                    val firstChild = layout.getChildAt(0)
                    val lastChild = layout.getChildAt(count - 1)
                    var startY = firstChild.top - firstChild.marginTop - scrollView.paddingTop
                    var endY = lastChild.bottom + lastChild.marginBottom + scrollView.paddingBottom

                    if (startY < 0) {
                        startY = 0
                    }
                    if (endY > endScrollY) {
                        endY = endScrollY
                    }
                    if (itemTop < 0) {
                        itemTop = 0
                        itemBottom = itemTop + itemHeight
                    }
                    if (itemBottom > endScrollY) {
                        itemBottom = endScrollY
                        itemTop = itemBottom - itemHeight
                    }
//                    if (scrollY <= startY) {
//                        if (itemTop < scrollY) {
//                            itemTop = scrollY
//                            itemBottom = itemTop + itemHeight
//                        }
//                    } else {
//                        if (itemTop < startY) {
//                            itemTop = startY
//                            itemBottom = itemTop + itemHeight
//                        }
//                    }
//                    if (endScrollY >= endY) {
//                        if (itemBottom > endScrollY) {
//                            itemBottom = endScrollY
//                            itemTop = itemBottom - itemHeight
//                        }
//                    } else {
//                        if (itemBottom > endY) {
//                            itemBottom = endY
//                            itemTop = itemBottom - itemHeight
//                        }
//                    }
                    logD("handLongClickEvent", "ACTION_MOVE [h:$itemHeight](t:$itemTop, b:$itemBottom) (sh, eh):($scrollHeight, $maxHeight) scrollY($scrollY, $endScrollY) startY($startY, $endY) dy:$dy = ($y - $rawY)")
                }

                // scrollView 自动滚的边界值
                val scrollUpBoundaryLow = scrollHeight * 0.3
                val scrollUpBoundary = scrollHeight * 0.25
                val scrollUpBoundaryHigh = scrollHeight * 0.2
                val scrollDownBoundaryLow = scrollHeight * 0.7
                val scrollDownBoundary = scrollHeight * 0.75
                val scrollDownBoundaryHigh = scrollHeight * 0.8

                val scrollLocation = IntArray(2)
                val itemLocation = IntArray(2)
                scrollView.getLocationOnScreen(scrollLocation)
                it.getLocationOnScreen(itemLocation)

                val topY = itemLocation[1] - scrollLocation[1]
                val bottomY = topY + itemHeight
                when {
                    topY < scrollUpBoundaryHigh -> {
                        if (isScroll(scrollView, layout, Orientation.UP)) {
                            scrollView.scroll(-12)
                        }
                    }
                    topY < scrollUpBoundary -> {
                        if (isScroll(scrollView, layout, Orientation.UP)) {
                            scrollView.scroll(-5)
                        }
                    }
                    topY < scrollUpBoundaryLow -> {
                        if (isScroll(scrollView, layout, Orientation.UP)) {
                            scrollView.scroll(-2)
                        }
                    }
                    bottomY > scrollDownBoundaryHigh -> {
                        if (isScroll(scrollView, layout, Orientation.DOWN)) {
                            scrollView.scroll(12)
                        }
                    }
                    bottomY > scrollDownBoundary -> {
                        if (isScroll(scrollView, layout, Orientation.DOWN)) {
                            scrollView.scroll(5)
                        }
                    }
                    bottomY > scrollDownBoundaryLow -> {
                        if (isScroll(scrollView, layout, Orientation.DOWN)) {
                            scrollView.scroll(2)
                        }
                    }
                    else -> {
                    }
                }
                logE("handLongClickEvent", "ACTION_MOVE (topY, bottomY)($topY, $bottomY) ($scrollUpBoundaryHigh, $scrollUpBoundary, $scrollUpBoundaryLow, $scrollDownBoundaryHigh, $scrollDownBoundary, $scrollDownBoundaryLow)")

                it.layout(it.left, itemTop, it.right, itemBottom)
                // 调用拖拽回调
                dragChange?.invoke(it, dy)
            }
        }
    }

    private fun resetCancel() {
        parent.requestDisallowInterceptTouchEvent(false)
        longClick = false
    }

    private fun longClickUp() {
        parent.requestDisallowInterceptTouchEvent(false)
        if (longClick) {
            longClick = false
            itemView?.notifyStateAll(state = EditorState.NORMAL)
        }
    }

    private fun isScroll(scrollView: ScrollView, layout: EditorLayout, orientation: Orientation): Boolean {
        val scrollHeight = scrollView.height
        val scrollY = scrollView.scrollY
        val endScrollY = scrollY + scrollHeight// - layout.paddingBottom
//        val maxHeight = layout.height
        val count = layout.childCount
        if (count > 0) {
//            val firstChild = layout.getChildAt(0)
//            val lastChild = layout.getChildAt(count - 1)
//            val startY = firstChild.top - firstChild.marginTop - scrollView.paddingTop
//            val endY = lastChild.bottom + lastChild.marginBottom + scrollView.paddingBottom

            if (Orientation.UP == orientation) {
                logW("isScroll", "  UP (scrollY, endScrollY)($scrollY, $endScrollY)")
                // 向上滚动
                if (scrollY <= 0) {
                    return false
                }
            } else if (Orientation.DOWN == orientation) {
                logW("isScroll", "DOWN (scrollY, endScrollY)($scrollY, $endScrollY)")
                // 向下滚动
                if (scrollY >= endScrollY) {
                    return false
                }
            }
        }
        return true
    }

    enum class Orientation {
        UP,
        DOWN
    }
}