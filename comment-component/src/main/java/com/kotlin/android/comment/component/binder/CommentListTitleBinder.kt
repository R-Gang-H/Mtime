package com.kotlin.android.comment.component.binder

import android.graphics.Typeface
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.databinding.ItemCommentListTitleBinding
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by lushan on 2020/8/7
 * 评论列表头 最新/最热
 */
open class CommentListTitleBinder(var bean: CommentTitleViewBean, var selectListener: ((Boolean) -> Unit)? = null) : MultiTypeBinder<ItemCommentListTitleBinding>() {
    override fun layoutId(): Int = R.layout.item_comment_list_title

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommentListTitleBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemCommentListTitleBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.hotTv.setTypeface(null, if (bean.isNew) Typeface.NORMAL else Typeface.BOLD)
        binding.newTv.setTypeface(null, if (bean.isNew) Typeface.BOLD else Typeface.NORMAL)

        binding.hotTv.onClick {
            selectListener?.invoke(false)
            bean.isNew = false
//            notifyAdapterSelfChanged()
            onClick(it)
        }

        binding.newTv.onClick {
            bean.isNew = true
            selectListener?.invoke(true)
//            notifyAdapterSelfChanged()
            onClick(it)
        }
    }

}