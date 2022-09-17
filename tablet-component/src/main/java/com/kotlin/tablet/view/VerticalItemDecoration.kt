package com.kotlin.tablet.view

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getColor

class VerticalItemDecoration(
    val edge: Float = 0f,
    val edgeLeft: Float = 15.dpF,
    val edgeRight: Float = 15.dpF,
    val edgeColorRes: Int? = android.R.color.transparent,
    val cornerRadius: Float = 0f,
) : RecyclerView.ItemDecoration() {

    private var mPaint: Paint = Paint()

    init {
        mPaint.apply {
            edgeColorRes?.apply {
                color = getColor(edgeColorRes)
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        val left = parent.paddingLeft + edgeLeft
        val right = parent.width - parent.paddingRight - edgeRight
        for (i in (0 until childCount - 1)) {
            val view = parent.getChildAt(i)
            val top = view.bottom.toFloat()
            val bottom = view.bottom.toFloat() + edge
            c.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, mPaint)
        }
    }

}