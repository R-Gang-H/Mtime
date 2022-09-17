package com.kotlin.android.card.monopoly.widget.suit.gallery

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * 套装轮播图条目装饰：
 *
 * Created on 2020/9/9.
 *
 * @author o.s
 */
class SuitGalleryItemDecoration(
    private val leftPadding: Int,
    private val topPadding: Int,
    private val rightPadding: Int,
    private val bottomPadding: Int
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        if (view.id == 0) {
//            return
//        }
        outRect.left = leftPadding
        outRect.top = topPadding
        outRect.right = rightPadding
        outRect.bottom = bottomPadding
    }
}