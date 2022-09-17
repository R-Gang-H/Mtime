package com.kotlin.android.comment.component.binder

import android.content.DialogInterface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.databinding.ItemCommentReplyDetailBinding
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.web.component.html.TYPE_ARTICLE
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * create by lushan on 2020/8/20
 * description: 评论详情上方布局
 */
class CommentReplyDetailBinder(var bean: CommentViewBean) : MultiTypeBinder<ItemCommentReplyDetailBinding>() {
    private val mainProvider: IMainProvider? = getProvider(IMainProvider::class.java)

    override fun layoutId(): Int = R.layout.item_comment_reply_detail

    fun updateShowTriangle(isShowTriangle: Boolean){
        "updateReplyEmpty== isShowTriangle:$isShowTriangle".e()
        this.bean.isShowTriangle = isShowTriangle
        binding?.trangleIv?.visible(isShowTriangle)
        notifyAdapterSelfChanged()
    }
    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommentReplyDetailBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemCommentReplyDetailBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        val delete = getString(R.string.comment_delete)
        val spannableString = SpannableString(delete)
        spannableString.setSpan(UnderlineSpan(), 0, delete.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.deleteTv.text = spannableString

        getDrawable(R.drawable.ic_comment_reply)?.mutate()?.apply {
            setBounds(0, 0, intrinsicWidth / 2, intrinsicWidth / 2)
            setTint(getColor(R.color.color_8798af))
        }?.also {
            binding.replyBtn.setCompoundDrawables(it, null, null, null)
        }

        getDrawable(R.drawable.ic_like)?.mutate()?.apply {
            setTint(getColor(if (bean.isLike()) R.color.color_20a0da else R.color.color_8798af))
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }?.also {
            binding.likeTv.setCompoundDrawables(it, null, null, null)
        }
        binding.webView.setData(content = bean.commentContent,ugcType = TYPE_ARTICLE,isPostComment = true)
    }

    fun updatePraiseUp() {
        bean.userPraised = if (bean.isLike()) 0L else CommentList.Item.USER_PRAISE_UP
        if (bean.isLike()) {
            bean.likeCount++
        } else {
            bean.likeCount--
        }
        notifyAdapterSelfChanged()
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.commentIvCV -> {//评论图片
                mainProvider?.startPhotoDetailActivity(arrayListOf(bean.commentPic), 0)
            }
            R.id.userPicIv, R.id.userNameTv -> {
                val communityPersonProvider: ICommunityPersonProvider? = getProvider(
                    ICommunityPersonProvider::class.java)
                communityPersonProvider?.startPerson(bean.userId)
            }
            R.id.deleteTv->{//删除按钮,显示删除提示弹框
                BaseDialog.Builder(context = view.context).setTitle("")
                        .setContent(R.string.comment_is_delete_comment)
                        .setPositiveButton(R.string.widget_sure, DialogInterface.OnClickListener { p0, p1 ->
                            super.onClick(view)
                            p0.cancel()

//                            notifyAdapterSelfRemoved()
                        }).setNegativeButton(R.string.widget_cancel, DialogInterface.OnClickListener { p0, p1 ->
                            p0.cancel()
                        })
                        .create().show()
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}