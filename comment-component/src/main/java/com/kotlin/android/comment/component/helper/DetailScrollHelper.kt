package com.kotlin.android.comment.component.helper

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.widget.TopSmoothScroller
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/10/21
 * description:
 */
class DetailScrollHelper(var rv: RecyclerView?) {
    private var isScrollToComment = false//是否点击了回复

    /**
     * 第一次点击评论按钮，滚动到评论标题处
     * 再次点击回到第一条
     */
    fun handleScrollToComment(
        context: Context,
        binderList: MutableList<MultiTypeBinder<*>>,
        isPk: Boolean = false
    ) {
        val linearLayoutManager = rv?.layoutManager as? LinearLayoutManager
        val findFirstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition() ?: 0

        val firstCommentBinderIndex = firstCommentBinderIndex(binderList, isPk)
        "findFirstVisibleItemPosition:$findFirstVisibleItemPosition == firstCommentBinderIndex:$firstCommentBinderIndex ==linearLayoutManager?.findFirstVisibleItemPosition():${linearLayoutManager?.findFirstVisibleItemPosition()}".e()
//        articleRv?.moveToPosition()
        if (firstCommentBinderIndex < 0) {
            isScrollToComment = !isScrollToComment
            return
        }
        val topSmoothScroller = TopSmoothScroller(context)
        topSmoothScroller.targetPosition = if (isScrollToComment) 0 else firstCommentBinderIndex
        rv?.layoutManager?.startSmoothScroll(topSmoothScroller)
        isScrollToComment = !isScrollToComment
    }

    private fun firstCommentBinderIndex(
        binderList: MutableList<MultiTypeBinder<*>>,
        isPk: Boolean
    ): Int {
        if (isPk) {
            return binderList.size + 1
        }
        if (binderList.isEmpty()) return 2//此时为相册
        return binderList.indexOfFirst { it is CommentListBinder || it is CommentListTitleBinder }
    }

    /**
     * 第一次进入是否要滑动到评论处
     */
    fun firstInAndScrollToComment(
        context: Context,
        hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf(),
        binderList: MutableList<MultiTypeBinder<*>> = mutableListOf(),
        needScrollToComment: Boolean = false,
        isPk: Boolean = false
    ): Boolean {
        return if (((isPk || hotCommentBinderList.isNotEmpty()) && binderList.size > hotCommentBinderList.size) && needScrollToComment) {
            handleScrollToComment(context, binderList, isPk)
            false
        } else {
            needScrollToComment
        }

    }

}