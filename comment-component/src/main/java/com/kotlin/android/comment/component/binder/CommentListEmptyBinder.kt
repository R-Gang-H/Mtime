package com.kotlin.android.comment.component.binder

import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.databinding.ItemNoCommentBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/23
 * description:没有评论的空Binder
 */
class CommentListEmptyBinder :MultiTypeBinder<ItemNoCommentBinding>() {
    override fun layoutId(): Int = R.layout.item_no_comment

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommentListEmptyBinder
    }
}