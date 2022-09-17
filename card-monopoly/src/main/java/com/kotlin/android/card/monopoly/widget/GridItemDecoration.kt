package com.kotlin.android.card.monopoly.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @desc gridlayout的分割线
 * @author zhangjian
 * @date 2020/9/11 17:08
 */
class GridItemDecoration(private val mDividerWidth: Int, @ColorInt color: Int) : RecyclerView.ItemDecoration() {
    private var mPaint: Paint? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        var itemPosition: Int = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        var spanCount = getSpanCount(parent)
        var childCount: Int? = parent.adapter?.itemCount
        var isLastRow = childCount?.let { isLastRow(parent, itemPosition, spanCount, it) }
        var isLastColumn = childCount?.let { isLastColumn(parent, itemPosition, spanCount, it) }
        var left: Int
        var right: Int
        var bottom: Int
        var eachWidth = (spanCount - 1) * mDividerWidth / spanCount
        var dl = mDividerWidth - eachWidth
        left = itemPosition % spanCount * dl
        right = eachWidth - left
        bottom = mDividerWidth
        if (isLastRow == true) {
            bottom = 0
        }
        if (isLastColumn == true) {
            right = 0
        }
        outRect.set(left, 0, right, bottom)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        draw(c, parent)
    }

    //绘制横向 item 分割线
    private fun draw(canvas: Canvas, parent: RecyclerView) {
        val childSize: Int = parent.childCount
        for (i in 0 until childSize) {
            val child: View = parent.getChildAt(i)
            val layoutParams: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams

            //画水平分隔线
            var left: Int = child.left
            var right: Int = child.right
            var top: Int = child.bottom + layoutParams.bottomMargin
            var bottom = top + mDividerWidth
            mPaint?.let { canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), it) }

            //画垂直分割线
            top = child.top
            bottom = child.bottom + mDividerWidth
            left = child.right + layoutParams.rightMargin
            right = left + mDividerWidth
            mPaint?.let { canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), it) }

        }
    }

    private fun isLastColumn(parent: RecyclerView, pos: Int, spanCount: Int,
                             childCount: Int): Boolean {
        var childCount = childCount
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) { // 如果是最后一列，则不需要绘制右边
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation: Int = layoutManager.orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) // 如果是最后一列，则不需要绘制右边
                {
                    return true
                }
            } else {
                childCount -= childCount % spanCount
                if (pos >= childCount) // 如果是最后一列，则不需要绘制右边
                    return true
            }
        }
        return false
    }

    private fun isLastRow(parent: RecyclerView, pos: Int, spanCount: Int,
                          childCount: Int): Boolean {
        var childCount = childCount
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            // childCount = childCount - childCount % spanCount;
            val lines = if (childCount % spanCount == 0) childCount / spanCount else childCount / spanCount + 1
            return lines == pos / spanCount + 1
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation: Int = layoutManager.orientation
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount -= childCount % spanCount
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) return true
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.color = color
        mPaint?.style = Paint.Style.FILL
    }
}
