package com.kotlin.android.comment.component.binder

import android.content.DialogInterface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.comment.component.*
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.databinding.ItemCommentListBinding
import com.kotlin.android.app.data.annotation.GROUP_ROLE_BLACKLIST
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.comment.component.helper.setTvUnderline
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * Created by lushan on 2020/8/6
 * 评论列表
 */
open class CommentListBinder(var context: FragmentActivity, var bean: CommentViewBean) : MultiTypeBinder<ItemCommentListBinding>() {
    private val commentProvider: ICommentProvider? = getProvider(ICommentProvider::class.java)
    private val mainProvider: IMainProvider? = getProvider(IMainProvider::class.java)
    private var isBlackUser:Boolean = false//帖子详情评论列表使用，当前用户是否是黑名单用户，传递给评论详情
    private var familyStatus:Long = 0L//当前用户是否加入此家族 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
    private var userGroupRole:Long = 0L//当前用户家族权限 APPLICANT(-1, "申请者"), OWNER(1, "族长"), ADMINISTRATOR(2, "管理员"), MEMBER(3, "普通成员"), BLACKLIST(4, "黑名单");
    private var familyPostStatus:Long? =0L//家族的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论");
    private var groupId:Long? = 0L//家族id
    private var commentPmsn: Long? = 1L//评论权限 1允许任何人 2禁止所有人
    fun setBlackUser(familyStatus:Long,userGroupRole:Long, familyPostStatus: Long, groupId: Long,commentPmsn: Long?){
        this.familyStatus = familyStatus
        this.userGroupRole = userGroupRole
        this.isBlackUser = familyStatus == CommunityContent.GROUP_JOIN_BLACK_NAME || userGroupRole == GROUP_ROLE_BLACKLIST
        this.familyPostStatus = familyPostStatus
        this.groupId = groupId
        this.commentPmsn = commentPmsn
    }

    fun setCommentPmsn(commentPmsn: Long?){
        this.commentPmsn = commentPmsn
    }


    override fun layoutId(): Int {
        return R.layout.item_comment_list
    }

    fun upatePraiseUp() {
        bean.userPraised = if (bean.isLike()) 0L else CommentList.Item.USER_PRAISE_UP
        if (bean.isLike()) {
            bean.likeCount++
        } else {
            bean.likeCount--
        }
        notifyAdapterSelfChanged()
    }


    override fun onBindViewHolder(binding: ItemCommentListBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        "onBindViewHolderposition:$position==binding:$binding".e()
        binding.apply {
            bean.replyCount > 0L
            when (bean.replyList.size) {
                0 -> {//没有回复
                    replyRL.gone()
                    firstItemLL.gone()
                    secondItemLL.gone()
                    spliteLineView.gone()
                    replyBtn.gone()
                    setFirseItemContent(this, null)
                    setSecondItemContent(this, null)
                }
                1 -> {//只有一条回复
                    replyRL.visible()
                    firstItemLL.visible()
                    secondItemLL.gone()
                    spliteLineView.gone()
                    replyBtn.gone()
                    setFirseItemContent(this, bean.replyList[0])
                    setSecondItemContent(this, null)
                }
                2 -> {//两条回复
                    setFirseItemContent(this, bean.replyList[0])
                    setSecondItemContent(this, bean.replyList[1])
                    replyRL.visible()
                    firstItemLL.visible()
                    secondItemLL.visible()
                    spliteLineView.gone()
                    replyBtn.gone()
                }
                else -> {//超过两条回复
                    setFirseItemContent(this, bean.replyList[0])
                    setSecondItemContent(this, bean.replyList[1])
                    replyRL.visible()
                    firstItemLL.visible()
                    secondItemLL.visible()
                    spliteLineView.visible()
                    replyBtn.visible()

                }
            }
            ShapeExt.setShapeColorAndCorner(replyRL, R.color.color_f2f3f6, 4.dp)
            replyBtn.apply {
                visible(bean.replyCount > 2L)
                spliteLineView.visible(bean.replyCount > 2L)
                text = getString(R.string.comment_reply_format, bean.replyCount - 2)
                //                onClick {//点击查看n条回复
                //                    showToast("查看${bean.replyList.size}条回复")
                //                }
            }
            val drawable = getDrawable(R.drawable.ic_like)?.mutate()
            drawable?.apply {
                val wrapDrawable = DrawableCompat.wrap(this)
                DrawableCompat.setTint(wrapDrawable, getColor(if (bean.isLike()) R.color.color_20a0da else R.color.color_8798af))
                wrapDrawable.setBounds(0, 0, wrapDrawable.intrinsicWidth, wrapDrawable.intrinsicHeight)
                likeTv.setCompoundDrawables(wrapDrawable, null, null, null)

            }
            binding.deleteTv.setTvUnderline(R.string.comment_delete)
            binding.expandTv.setTvUnderline(R.string.comment_list_expand)
        }

        getDrawable(R.drawable.ic_comment_reply)?.apply {
            setBounds(0, 0, intrinsicWidth / 2, intrinsicWidth / 2)
            setTint(getColor(R.color.color_8798af))
        }?.also {
            binding.comentCountTv.setCompoundDrawables(it, null, null, null)
        }
        binding.contentTv?.text = bean.commentContent
        setExpandVisibility()
    }
    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommentListBinder && other.bean != bean
    }

    /**
     * 文本内容超过三行要显示展开按钮
     */
    private fun setExpandVisibility() {
        binding?.contentTv?.maxLines = 4//避免第二次刷新的时候maxLine为3导致展示显示不出来
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                if (binding?.contentTv?.lineCount?:0>3){
                    binding?.contentTv?.maxLines = 3
                    binding?.expandTv?.visible()
                }else{
                    binding?.contentTv?.maxLines = binding?.contentTv?.lineCount?:0
                    binding?.expandTv?.gone()
                }
                binding?.contentTv?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        }
        binding?.contentTv?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }


    private fun setFirseItemContent(binding: ItemCommentListBinding, replayBean: ReplyViewBean?) {
        binding.apply {
            firstUserNameTv.text = "${replayBean?.userName.orEmpty()}:"
            firstReplyConentTv.text = replayBean?.replyContent.orEmpty()
        }

    }

    private fun setSecondItemContent(binding: ItemCommentListBinding, replayBean: ReplyViewBean?) {
        binding.apply {
            secondUserNameTv.text = "${replayBean?.userName.orEmpty()}:"
            secondReplyConentTv.text = replayBean?.replyContent.orEmpty()
        }
    }


    override fun onClick(view: View) {
//        super.onClick(view)
        when (view.id) {
            R.id.commentRootCL -> {//跳转到评论详情
                commentProvider?.startComment(bean.commentId, bean.type,isUserBlack = isBlackUser,familyStatus = familyStatus,userGroupRole = userGroupRole,commentPmsn = commentPmsn,familyPostStatus = familyPostStatus?:0L,groupId = groupId?:0L)
            }
//            R.id.likeTv -> {//点赞按钮
//
////                viewModel?.praiseUpOrCancel(getPraiseType(bean.type), bean.commentId, bean.isLike, this)
//
//            }
            R.id.deleteTv -> {//删除按钮

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
            R.id.commentIvCV -> {//图片，跳转到图片大图
                mainProvider?.startPhotoDetailActivity(arrayListOf(bean.commentPic), 0)
            }
            R.id.userPicIv, R.id.userNameTv -> {//跳转到用户主页
                val communityPersonProvider: ICommunityPersonProvider? = getProvider(
                    ICommunityPersonProvider::class.java)
                communityPersonProvider?.startPerson(bean.userId)
            }
            else -> super.onClick(view)
        }


    }



}