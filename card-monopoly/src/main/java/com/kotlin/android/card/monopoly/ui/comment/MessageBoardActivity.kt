package com.kotlin.android.card.monopoly.ui.comment

import android.content.Intent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ActMessageBoardBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bind.CommentView
import com.kotlin.android.comment.component.bind.binder.CommentItemBinder
import com.kotlin.android.comment.component.bind.binder.CommentTitleBinder
import com.kotlin.android.comment.component.bind.viewmodel.CommentViewModel
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_POST
import com.kotlin.android.app.data.entity.comment.PostComment
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.ktx.ext.KEY_USER_ID
import com.kotlin.android.ktx.ext.KEY_USER_NAME
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle

/**
 * 留言板
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_MESSAGE_BOARD)
class MessageBoardActivity : BaseVMActivity<CardMonopolyApiViewModel, ActMessageBoardBinding>() {

    private var userId: Long = 0L
    private var userName: String = ""
    private val mData by lazy { ArrayList<CommentViewBean>() }
    private val commentViewModel by lazy {
        viewModels<CommentViewModel>().value
    }
    private val personProvider by lazy {
        getProvider(ICommunityPersonProvider::class.java)
    }
    private val commentProvider by lazy {
        getProvider(ICommentProvider::class.java)
    }
    private var pageIndex: Long = 1L
    private var pageSize: Long = 20L
    private var mTotalCount: Long = 0
    private var mCommentList: CommentList? = null
        set(value) {
            field = value
            if (pageIndex == 1L) {
                mData.clear()
            }
            value?.items?.apply {
                mTotalCount = value.totalCount.toLong()
                val unReleased: ArrayList<CommentViewBean> = ArrayList()
                forEach {
                    if (it.releasedState) {
                        mData.add(CommentViewBean.obtain(it))
                    } else {
                        unReleased.add(CommentViewBean.obtain(it))
                    }
                }
                if (unReleased.isNotEmpty()) {
                    mData.addAll(0, unReleased)
                }
            }
            if (value?.hasNext == true) {
                pageIndex++
            }
        }

    override fun getIntentData(intent: Intent?) {
        intent?.apply {
            userId = intent.getLongExtra(KEY_USER_ID, 0L)
            userName = intent.getStringExtra(KEY_USER_NAME).orEmpty()
        }
    }

    override fun initView() {
        initTitleView()
        initCommentView()
    }

    override fun initData() {
        loadCommentList()
    }

    override fun startObserve() {
        commentViewModel.uiState.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mCommentList = this
                    updateCommentView()
                }
            }
        }
        commentViewModel.deleteCommentUiState.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                }
            }
        }

        commentViewModel.praiseUpUiState.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                }
            }
        }
    }

    fun loadCommentList() {
        val isNew = mBinding?.commentView?.isNew ?: false
        val log = if(isNew) "最新" else "最热"
        "loadCommentList: $log".e()
        if (pageIndex == 1L) {
            // 加载第一页数据
            if (isLogin()) {
                commentViewModel.loadCommentList(
                    isNewComment = isNew,
                    isReleased = false,
                    postComment = PostComment(
                        objType = PRAISE_OBJ_TYPE_POST, //PRAISE_OBJ_TYPE_CARD_USER,
                        objId = userId,
                        pageIndex = pageIndex,
                        pageSize = 10L
                    )
                )
            }
        }
        commentViewModel.loadCommentList(
            isNewComment = isNew,
            isReleased = true,
            postComment = PostComment(
                objType = PRAISE_OBJ_TYPE_POST,
                objId = userId,
                pageIndex = pageIndex,
                pageSize = pageSize
            )
        )
    }

    private fun updateCommentView() {
        mBinding?.commentView?.apply {
            setTitle(mTotalCount)
            setData(mData)
        }
    }

    private fun initTitleView() {
        mBinding?.titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.NORMAL)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle("${userName}的留言板") {

            }
//            setRightIcon(
//                resId = R.drawable.ic_title_bar_more_light,
//                reverseResId = R.drawable.ic_title_bar_more_dark
//            ) {
//                showFunctionMenuDialog(
//                    dismiss = {
//                        syncStatusBar()
//                    }
//                )
//            }
//            setScrollView(scrollView)
        }
    }

    private fun initCommentView() {
        mBinding?.commentView?.apply {
            actionTitle = {
                when (it.view) {
                    it.holder.binding.newTv -> {
                        newComment(commentView = this, holder = it.holder)
                    }
                    it.holder.binding.hotTv -> {
                        hotComment(commentView = this, holder = it.holder)
                    }
                    else -> {}
                }
            }
            actionItem = {
                when (it.view) {
                    it.holder.binding.userPicIv,
                    it.holder.binding.userNameTv,
                    it.holder.binding.firstUserNameTv,
                    it.holder.binding.secondUserNameTv -> {
                        gotoUserPage(it.holder)
                    }
                    it.holder.binding.commentRootCL -> {
                        gotoCommentDetailPage(it.holder)
                    }
                    it.holder.binding.likeTv -> {
                        praiseUp(it.holder)
                    }
                    it.holder.binding.deleteTv -> {
                        deleteComment(commentView = this, holder = it.holder)
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * 最新评论
     */
    private fun newComment(commentView: CommentView, holder: CommentTitleBinder.Holder) {
        holder.apply {
            data?.isNew = true
            commentView.isNew = true
            notifyItemChanged()
        }
        loadCommentList()
    }

    /**
     * 最热评论
     */
    private fun hotComment(commentView: CommentView, holder: CommentTitleBinder.Holder) {
        holder.apply {
            data?.isNew = false
            commentView.isNew = false
            notifyItemChanged()
        }
        loadCommentList()
    }

    /**
     * 去用户详情页
     */
    private fun gotoUserPage(holder: CommentItemBinder.Holder) {
        holder.data?.apply {
            personProvider?.startPerson(userId = userId)
        }
    }

    /**
     * 去评论详情页
     */
    private fun gotoCommentDetailPage(holder: CommentItemBinder.Holder) {
        holder.data?.apply {
            commentProvider?.startComment(
                objType = type,
                commentId = commentId,
            )
        }
    }

    /**
     * 点赞/取消点赞
     */
    private fun praiseUp(holder: CommentItemBinder.Holder) {
        holder.apply {
            data?.apply {
                // action 动作 1.点赞 2.取消点赞
                val action = if (userPraised == 1L) {
                    userPraised = null
                    likeCount--
                    2L
                } else {
                    userPraised = 1L
                    likeCount++
                    1L
                }
                commentViewModel.praiseUp(
                    action = action,
                    objType = CommConstant.getPraiseUpType(type, CommConstant.COMMENT_PRAISE_ACTION_COMMENT),
                    objId = commentId
                )
            }
            notifyItemChanged()
        }
    }

    /**
     * 删除评论
     */
    private fun deleteComment(commentView: CommentView, holder: CommentItemBinder.Holder) {
        holder.apply {
            com.kotlin.android.widget.dialog.showDialog(
                context = this@MessageBoardActivity,
                content = com.kotlin.android.comment.component.R.string.comment_is_delete_comment
            ) {
                data?.apply {
                    commentViewModel.deleteComment(
                        objType = type,
                        commentId = commentId,
                    )
                    val position = holder.mPosition
                    if (position > -1) {
                        commentView.notifyItemRemoved(position)
                    }
                }
            }
        }
    }

}