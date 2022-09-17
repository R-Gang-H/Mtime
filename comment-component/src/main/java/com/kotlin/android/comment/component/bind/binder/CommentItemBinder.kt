package com.kotlin.android.comment.component.bind.binder

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import androidx.core.graphics.drawable.DrawableCompat
import com.kotlin.android.bind.holder.ItemViewBindingHolder
import com.kotlin.android.bind.item.ItemViewBindingBinder
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.databinding.BindItemCommentItemBinding
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.span.toUnderLine
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.widget.BR

/**
 * 评论 Item 视图
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
class CommentItemBinder :
    ItemViewBindingBinder<CommentViewBean, BindItemCommentItemBinding, CommentItemBinder.Holder>() {

    class Holder(binding: BindItemCommentItemBinding) :
        ItemViewBindingHolder<CommentViewBean, BindItemCommentItemBinding>(binding) {
        override fun onBind(
            binding: BindItemCommentItemBinding,
            position: Int,
            item: CommentViewBean
        ) {
            binding.setVariable(BR.data, item)
            val replyCount = item.replyCount
            val replyList = item.replyList
            binding.apply {
                replyCount > 0L
                // 处理回复内容
                when (replyList.size) {
                    0 -> {//没有回复
                        replyRL.gone()
                        firstItemLL.gone()
                        secondItemLL.gone()
                        spliteLineView.gone()
                        replyBtn.gone()
                        setFirstItemContent(null)
                        setSecondItemContent(null)
                    }
                    1 -> {//只有一条回复
                        replyRL.visible()
                        firstItemLL.visible()
                        secondItemLL.gone()
                        spliteLineView.gone()
                        replyBtn.gone()
                        setFirstItemContent(replyList[0])
                        setSecondItemContent(null)
                    }
                    2 -> {//两条回复
                        setFirstItemContent(replyList[0])
                        setSecondItemContent(replyList[1])
                        replyRL.visible()
                        firstItemLL.visible()
                        secondItemLL.visible()
                        spliteLineView.gone()
                        replyBtn.gone()
                    }
                    else -> {//超过两条回复
                        setFirstItemContent(replyList[0])
                        setSecondItemContent(replyList[1])
                        replyRL.visible()
                        firstItemLL.visible()
                        secondItemLL.visible()
                        spliteLineView.visible()
                        replyBtn.visible()
                    }
                }
                // 回复布局
                replyRL.setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 4.dpF
                )
                // 查看更多回复
                replyBtn.apply {
                    visible(replyCount > 2L)
                    spliteLineView.visible(replyCount > 2L)
                    text = getString(R.string.comment_reply_format, replyCount - 2)
//                    setOnClickListener {
//                        // 点击查看n条回复
////                        showToast("查看${replyList.size}条回复")
//                        gotoMessageBoard()
//                    }
                }
                // 点赞
                getDrawable(R.drawable.ic_like)?.mutate()?.apply {
                    val wrapDrawable = DrawableCompat.wrap(this).apply {
                        DrawableCompat.setTint(
                            this,
                            getColor(if (item.isLike()) R.color.color_20a0da else R.color.color_8798af)
                        )
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                    }
                    likeTv.setCompoundDrawables(wrapDrawable, null, null, null)

                }
                //删除/展开
                deleteTv.text = getString(R.string.comment_delete).toSpan().toUnderLine()
                expandTv.text = getString(R.string.comment_list_expand).toSpan().toUnderLine()
            }

            getDrawable(R.drawable.ic_comment_reply)?.apply {
                setBounds(0, 0, intrinsicWidth / 2, intrinsicWidth / 2)
                setTint(getColor(R.color.color_8798af))
            }?.also {
                binding.commentCountTv.setCompoundDrawables(it, null, null, null)
            }
            binding.contentTv.text = item.commentContent
            setExpandVisibility()

            /**
             * 点击跳转用户详情
             */
            setOnItemClick(binding.userPicIv)
            setOnItemClick(binding.userNameTv)
            setOnItemClick(binding.firstUserNameTv)
            setOnItemClick(binding.secondUserNameTv)

            /**
             * 点击跳转评论详情
             */
            setOnItemClick(binding.commentRootCL)

            /**
             * 点击点赞/评论小图标
             */
            setOnItemClick(binding.likeTv)

            /**
             * 评论小图标
             */
            setOnItemClick(binding.commentCountTv)

            /**
             * 删除评论
             */
            setOnItemClick(binding.deleteTv)
        }

        /**
         * 文本内容超过三行要显示展开按钮
         */
        private fun setExpandVisibility() {
            binding.contentTv.maxLines = 4//避免第二次刷新的时候maxLine为3导致展示显示不出来
            val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.contentTv.lineCount > 3) {
                        binding.contentTv.maxLines = 3
                        binding.expandTv.visible()
                    } else {
                        binding.contentTv.maxLines = binding.contentTv.lineCount
                        binding.expandTv.gone()
                    }
                    binding.contentTv.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            }
            binding.contentTv.viewTreeObserver?.addOnGlobalLayoutListener(listener)
        }

        @SuppressLint("SetTextI18n")
        private fun setFirstItemContent(replayBean: ReplyViewBean?) {
            binding.apply {
                firstUserNameTv.text = "${replayBean?.userName.orEmpty()}："
                firstReplyCommentTv.text = replayBean?.replyContent.orEmpty()
            }
        }

        @SuppressLint("SetTextI18n")
        private fun setSecondItemContent(replayBean: ReplyViewBean?) {
            binding.apply {
                secondUserNameTv.text = "${replayBean?.userName.orEmpty()}："
                secondReplyCommentTv.text = replayBean?.replyContent.orEmpty()
            }
        }

    }

}