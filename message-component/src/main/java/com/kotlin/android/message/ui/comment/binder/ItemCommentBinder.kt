package com.kotlin.android.message.ui.comment.binder

import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemCommentBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.comment.viewBean.CommentViewBean
import com.kotlin.android.message.widget.CommentTextView
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.showDialog

/**
 * Created by zhaoninglongfei on 2022/3/22
 *
 */
class ItemCommentBinder(val bean: CommentViewBean, private val delComment: (String) -> Unit) :
    MultiTypeBinder<MessageItemCommentBinding>() {
    override fun layoutId(): Int = R.layout.message_item_comment

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemCommentBinder
    }

    override fun onBindViewHolder(binding: MessageItemCommentBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.tvComment.addEllipsizeListener(object : CommentTextView.EllipsizeListener {
            override fun onExpand() {
                binding.tvExpand.visible()
                binding.tvExpand.text = getString(R.string.message_comment_pack_up)
            }

            override fun onCollapse() {
                binding.tvExpand.visible()
                binding.tvExpand.text = getString(R.string.message_comment_expand)
            }

            override fun onLoss() {
                binding.tvExpand.gone()
            }
        })
    }

    fun deleteComment(messageId: String): Boolean {
        showDialog(
            context = binding?.root?.context,
            content = getString(R.string.message_confirm_delete_comment),
        ) {
            delComment.invoke(messageId)
        }
        return true
    }

    fun jumpToUserHome() {
        MessageCenterJumper.jumpToUserHome(binding?.root?.context, bean.userId)
    }

    fun jumpToCommentDetail() {
        getProvider(ICommentProvider::class.java)?.startComment(
            objType = bean.contentType?.type ?: 0L,
            commentId = bean.commentId ?: 0L,
        )
    }

    fun expend() {
        binding?.tvComment?.toggleShowLines()
    }
}