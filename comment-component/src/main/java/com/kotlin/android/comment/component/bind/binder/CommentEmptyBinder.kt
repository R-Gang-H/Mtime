package com.kotlin.android.comment.component.bind.binder

import com.kotlin.android.bind.holder.ItemViewBindingHolder
import com.kotlin.android.bind.item.ItemViewBindingBinder
import com.kotlin.android.comment.component.databinding.BindItemCommentEmptyBinding
import com.kotlin.android.app.data.entity.comment.CommentEmpty

/**
 *
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
class CommentEmptyBinder : ItemViewBindingBinder<CommentEmpty, BindItemCommentEmptyBinding, CommentEmptyBinder.Holder>() {

    class Holder(binding: BindItemCommentEmptyBinding) : ItemViewBindingHolder<CommentEmpty, BindItemCommentEmptyBinding>(binding) {
        override fun onBind(binding: BindItemCommentEmptyBinding, position: Int, item: CommentEmpty) {

        }
    }

}