package com.kotlin.android.community.ui.follow.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/21
 */
class DividerItemDecoration(
        val dividerHeight: Int, //单位为dp
        @ColorRes val dividerColorRes: Int,
        @RecyclerView.Orientation val orientation: Int = RecyclerView.VERTICAL
): RecyclerView.ItemDecoration() {

    private var dividerPaint: Paint = Paint()
    var offsetStart: Int = 0 //单位为dp
    var offsetEnd: Int = 0 //单位为dp

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            val position = parent.getChildAdapterPosition(view)
            if (position != it.itemCount - 1) {
                if (orientation == RecyclerView.VERTICAL) {
                    outRect.bottom = dividerHeight.dp
                } else {
                    outRect.right = dividerHeight.dp
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == RecyclerView.VERTICAL) {
            drawVerticalDividers(c, parent)
        } else {
            drawHorizontalDividers(c, parent)
        }
    }

    private fun initPaintColor(context: Context) {
        dividerPaint.color = context.getColor(dividerColorRes)
    }

    private fun drawVerticalDividers(c: Canvas, parent: RecyclerView) {
        val childCount: Int = parent.childCount
        val left: Int = parent.paddingLeft + offsetStart.dp
        val right: Int = parent.width - parent.paddingRight - offsetEnd.dp

        for (i in 0 until childCount - 1) {
            val view: View = parent.getChildAt(i)
            val top = view.bottom.toFloat()
            val bottom = view.bottom + dividerHeight.toFloat()
            initPaintColor(parent.context)
            c.drawRect(left.toFloat(), top, right.toFloat(), bottom, dividerPaint)
        }
    }

    private fun drawHorizontalDividers(c: Canvas, parent: RecyclerView) {
        val childCount: Int = parent.childCount
        val top: Int = parent.paddingTop + offsetStart.dp
        val bottom: Int = parent.height - parent.paddingBottom - offsetEnd.dp

        for (i in 0 until childCount - 1) {
            val view: View = parent.getChildAt(i)
            val left = view.right.toFloat()
            val right = view.right + dividerHeight.toFloat()
            initPaintColor(parent.context)
            c.drawRect(left, top.toFloat(), right, bottom.toFloat(), dividerPaint)
        }
    }
}