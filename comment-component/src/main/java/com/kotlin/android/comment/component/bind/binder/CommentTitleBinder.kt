package com.kotlin.android.comment.component.bind.binder

import com.kotlin.android.bind.holder.ItemViewBindingHolder
import com.kotlin.android.bind.item.ItemViewBindingBinder
import com.kotlin.android.comment.component.databinding.BindItemCommentTitleBinding
import com.kotlin.android.app.data.entity.comment.CommentTitle

import com.kotlin.android.widget.BR
/**
 *
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
class CommentTitleBinder : ItemViewBindingBinder<CommentTitle, BindItemCommentTitleBinding, CommentTitleBinder.Holder>() {

    class Holder(binding: BindItemCommentTitleBinding) : ItemViewBindingHolder<CommentTitle, BindItemCommentTitleBinding>(binding) {
        override fun onBind(binding: BindItemCommentTitleBinding, position: Int, item: CommentTitle) {
            binding.setVariable(BR.data, item)
            setOnItemClick(binding.hotTv)
            setOnItemClick(binding.newTv)
        }
    }

}