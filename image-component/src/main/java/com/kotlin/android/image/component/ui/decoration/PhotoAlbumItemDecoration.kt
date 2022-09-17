package com.kotlin.android.image.component.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.dimension.dp

/**
 *
 * Created on 2020/7/23.
 *
 * @author o.s
 */
class PhotoAlbumItemDecoration(@IntRange(from = 2, to = 10) private val spanCount: Int) : RecyclerView.ItemDecoration() {
    private val totalEdge = 60.dp
    private val part = totalEdge / spanCount
    private val atomicPart = part / (spanCount - 1)
    private val edge = 15.dp
    private val edgeTop = 10.dp
    private val edgeBottom = 60.dp
    private val gap = 10.dp
    private val gapHalf = 5.dp

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val col = position % spanCount
        val row = position / spanCount
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

        /**
         * source: | _ _ _ | _ _ _ | _ _ _ | _ _ _
         * target: _ _ _ | _ _ | _ _ | _ _ | _ _ _
         */
        outRect.left = atomicPart * (spanCount - col - 1)

        outRect.top = edgeTop
        if (row == lastRow) {
            outRect.bottom = edgeBottom
        }

    }

}