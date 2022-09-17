package com.kotlin.android.card.monopoly.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView上下分割线：
 *
 * Created on 2020/12/17.
 *
 * @author o.s
 */
class TopAndBottomItemDecoration(
        val top: Int = 0,
        val bottom: Int = 0,
        val lastEdge: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val count = parent.adapter?.itemCount ?: 0
        val lastPosition = count - 1
        if (top > 0) {
            outRect.top = top
        }
        if (bottom > 0) {
            outRect.bottom = bottom
        }
        if (lastPosition == position && lastEdge > 0) {
            outRect.bottom = lastEdge
        }
    }

}