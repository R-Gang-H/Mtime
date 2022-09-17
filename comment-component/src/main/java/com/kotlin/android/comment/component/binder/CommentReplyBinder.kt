package com.kotlin.android.comment.component.binder

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.UnderlineSpan
import android.view.View
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.databinding.ItemCommentReplyItemBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/8/20
 * description:评论详情回复列表
 */
class CommentReplyBinder(var bean: ReplyViewBean) : MultiTypeBinder<ItemCommentReplyItemBinding>() {
    private val mainProvider: IMainProvider? = getProvider(IMainProvider::class.java)
    override fun layoutId(): Int = R.layout.item_comment_reply_item

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommentReplyBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemCommentReplyItemBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        val topCorner = if (position == 1) 4 else 0
        val bottomCorner = if (position == getItemCount() - 1) 4 else 0

        ShapeExt.setShapeCornerWithColor(binding.rootView, R.color.color_f2f3f6, topCorner, topCorner, bottomCorner, bottomCorner)

        getDrawable(R.drawable.ic_like)?.mutate()?.apply {
            setTint(getColor(if (bean.isLike) R.color.color_20a0da else R.color.color_8798af))
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }?.also {
            binding.likeTv.setCompoundDrawables(it, null, null, null)
        }

        getDrawable(R.drawable.ic_link_photo)?.apply {
            setBounds(0, 0, intrinsicWidth / 2, intrinsicHeight / 2)
        }?.also {
        binding.scanTv?.setCompoundDrawables(it,null,null,null)
        }
    }

    private fun getSpanable(): SpannableString {

        val content = "${bean.replyContent}${getString(R.string.comment_scan)}${getString(R.string.comment_scan_image)}"
        return SpannableString(content).apply {
            setSpan(ForegroundColorSpan(getColor(R.color.color_505e74)), 0, bean.replyContent.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val drawable = getDrawable(R.drawable.ic_link_photo)?.apply {
                setBounds(0, 0, intrinsicWidth / 2, intrinsicHeight / 2)
            }
            drawable?.apply {
                setSpan(ImageSpan(this, ImageSpan.ALIGN_CENTER), bean.replyContent.length, bean.replyContent.length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            setSpan(ForegroundColorSpan(getColor(R.color.color_8798af)), bean.replyContent.length + 1, content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            setSpan(UnderlineSpan(), bean.replyContent.length + 1, content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(object : ClickableSpan() {
                override fun onClick(p0: View) {
//                    showToast("跳转至图片预览页")
                    mainProvider?.startPhotoDetailActivity(arrayListOf(bean.picUrl), 0)
                }

            }, bean.replyContent.length, content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(object : UnderlineSpan() {
                override fun updateDrawState(ds: TextPaint) {
//                    super.updateDrawState(ds)
                    ds.color = getColor(R.color.color_8798af)
                }
            }, bean.replyContent.length + 1, content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        }


    }

    fun updatePraiseUp() {
        bean.isLike = bean.isLike.not()
        if (bean.isLike) {
            bean.praiseCount++
        } else {
            bean.praiseCount--
        }
        notifyAdapterSelfChanged()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.replyUserPicIv, R.id.userNameTv -> {
                val communityPersonProvider: ICommunityPersonProvider? = getProvider(
                    ICommunityPersonProvider::class.java)
                communityPersonProvider?.startPerson(bean.userId)
            }
            R.id.scanTv->{//查看图片
                mainProvider?.startPhotoDetailActivity(arrayListOf(bean.picUrl), 0)
            }
            else->{
                super.onClick(view)
            }
        }
    }


}