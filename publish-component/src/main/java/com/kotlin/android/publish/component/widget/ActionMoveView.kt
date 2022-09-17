package com.kotlin.android.publish.component.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.kotlin.android.ktx.ext.core.scroll
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w

/**
 * 移动事件视图
 *
 * Created on 2020/7/14.
 *
 * @author o.s
 */
class ActionMoveView : androidx.appcompat.widget.AppCompatImageView {

    private val tag = "    5 ->"

    /**
     * 拖拽改变
     */
    var dragChange: ((v: View, dy: Float) -> Unit)? = null

    private var itemView: PublishItemView? = null
    private var action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)? = null

    fun setAction(itemView: PublishItemView?,
                  action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)?) {
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
                "${this@ActionMoveView.tag} longClick start move".e()
                // 回调事件
                action?.invoke(this, ActionEvent.EVENT_MOVE)
                // 高亮itemView
                highlightView()
                // 更改长按状态
                longClick = true

                "${this@ActionMoveView.tag} longClick = $longClick".w()
                true
            } ?: false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        "$tag dispatchTouchEvent".i()
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    rawY = it.y
                    "$tag onTouchEvent ACTION_DOWN".e()
                }
                MotionEvent.ACTION_UP -> {
                    "$tag onTouchEvent ACTION_UP".e()
                    longClickUp()
                }
                MotionEvent.ACTION_CANCEL -> {
                    "$tag onTouchEvent ACTION_CANCEL".e()
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
        if (rawY == 0F) {
            rawY = event.y
        }
        if (longClick) {
            parent.requestDisallowInterceptTouchEvent(true)
            val y = event.y
            val dy = y - rawY
//            val itemView = parent.parent as PublishItemView
            itemView?.let {
                val layout = it.parent as PublishLayout
                val scrollView = layout.parent as PublishScrollView
                val scrollHeight = scrollView.height
                val itemHeight = it.height

                // 调整 ItemLayout top, bottom
                var itemTop = (it.top + dy).toInt()
                var itemBottom = itemTop + itemHeight

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
                    if (endY > maxHeight) {
                        endY = maxHeight
                    }
                    if (scrollY <= startY) {
                        if (itemTop < scrollY) {
                            itemTop = scrollY
                            itemBottom = itemTop + itemHeight
                        }
                    } else {
                        if (itemTop < startY) {
                            itemTop = startY
                            itemBottom = itemTop + itemHeight
                        }
                    }
                    if (endScrollY >= endY) {
                        if (itemBottom > endScrollY) {
                            itemBottom = endScrollY
                            itemTop = itemBottom - itemHeight
                        }
                    } else {
                        if (itemBottom > endY) {
                            itemBottom = endY
                            itemTop = itemBottom - itemHeight
                        }
                    }
                    "$tag onTouchEvent ACTION_MOVE dy = $dy = $y - $rawY :: [startY, scrollY, endScrollY, endY, scrollHeight] -> [$startY, $scrollY, $endScrollY, $endY, $scrollHeight] :: (itemTop, itemBottom) -> ($itemTop, $itemBottom)".w()
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
                            scrollView.scroll(-8)
                        }
                    }
                    topY < scrollUpBoundary -> {
                        if (isScroll(scrollView, layout, Orientation.UP)) {
                            scrollView.scroll(-4)
                        }
                    }
                    topY < scrollUpBoundaryLow -> {
                        if (isScroll(scrollView, layout, Orientation.UP)) {
                            scrollView.scroll(-2)
                        }
                    }
                    bottomY > scrollDownBoundaryHigh -> {
                        if (isScroll(scrollView, layout, Orientation.DOWN)) {
                            scrollView.scroll(8)
                        }
                    }
                    bottomY > scrollDownBoundary -> {
                        if (isScroll(scrollView, layout, Orientation.DOWN)) {
                            scrollView.scroll(4)
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
            val rLayout = parent?.parent?.parent as? PublishLayout
            rLayout?.state = State.NORMAL
        }
    }

    private fun isScroll(scrollView: PublishScrollView, layout: PublishLayout, orientation: Orientation): Boolean {
        val scrollHeight = scrollView.height
        val scrollY = scrollView.scrollY
        val endScrollY = scrollY + scrollHeight// - layout.paddingBottom
        val maxHeight = layout.height
        val count = layout.childCount
        if (count > 0) {
            val firstChild = layout.getChildAt(0)
            val lastChild = layout.getChildAt(count - 1)
            val startY = firstChild.top - firstChild.marginTop - scrollView.paddingTop
            val endY = lastChild.bottom + lastChild.marginBottom + scrollView.paddingBottom

            if (Orientation.UP == orientation) {
                // 向上滚动
                if (scrollY == 0) {
                    return false
                } else if (scrollY <= startY) {
                    return false
                }
            } else if (Orientation.DOWN == orientation) {
                // 向下滚动
                if (endScrollY >= maxHeight) {
                    return false
                } else if (endScrollY >= endY) {
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