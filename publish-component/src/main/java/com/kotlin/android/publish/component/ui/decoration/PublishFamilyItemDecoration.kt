package com.kotlin.android.publish.component.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.ktx.ext.dimension.dp

/**
 *
 * Created on 2020/7/22.
 *
 * @author o.s
 */
class PublishFamilyItemDecoration : RecyclerView.ItemDecoration() {
    private val leftMargin = 18.dp
    private val rightMargin = 8.dp

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val last = (parent.adapter?.itemCount ?: 0) - 1
        when (position) {
            0 -> {
                outRect.left = leftMargin
            }
            last -> {
                outRect.right = rightMargin
            }
            else -> {
//                outRect.left = 0
//                outRect.right = 0
            }
        }
    }
}