package com.kotlin.android.image.component.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView

/**
 * 均分装饰
 *
 * Created on 2020/11/04.
 *
 * @author o.s
 */
class AverItemDecoration(
        @IntRange(from = 2, to = 10) private val spanCount: Int,
        private val edge: Int,
        private val edgeTop: Int = edge,
        private val edgeBottom: Int = 0,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val row = position / spanCount
        val col = position % spanCount
        val aver = edge / spanCount.toFloat()
        if (row > 0) {
            outRect.top = edgeTop
        }
        outRect.left = (aver * col).toInt()
        outRect.right = (aver * (spanCount - 1 - col)).toInt()

        // 处理最后一行预留的底部空间高度
        if (edgeBottom > 0) {
            val count = parent.adapter?.itemCount ?: 0
            if (count <= 0) {
                return
            }

            val tempLastRow = count / spanCount
            // 行标从0开始
            val lastRow = if (count % spanCount == 0) {
                tempLastRow - 1
            } else {
                tempLastRow
            }
            if (row == lastRow) {
                outRect.bottom = edgeBottom
            }
        }
    }

}