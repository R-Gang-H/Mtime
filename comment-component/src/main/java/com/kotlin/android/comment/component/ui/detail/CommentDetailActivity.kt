package com.kotlin.android.comment.component.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentReplyBinder
import com.kotlin.android.comment.component.binder.CommentReplyDetailBinder
import com.kotlin.android.comment.component.databinding.ActivityCommentDetailBinding
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.annotation.GROUP_ROLE_BLACKLIST
import com.kotlin.android.app.data.annotation.JOIN_POST_JOIN_COMMENT
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.isShowSoftInput
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.user.afterLogin
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_comment_detail.*

/**
 * 评论详情
 */
@Route(path = RouterActivityPath.Comment.PAGE_COMMENT_DETAIL_ACTIVITY)
class CommentDetailActivity : BaseVMActivity<CommentDetailViewModel, ActivityCommentDetailBinding>() {

    companion object {
        const val COMMENT_ID = "comment_id"
        const val COMMENT_OBJECT_TYPE = "comment_object_type"
        const val COMMENT_IS_POSITIVE = "comment_is_positive"
        const val COMMENT_IS_USER_BLACK = "comment_is_user_black"
        const val COMMENT_FAMILYSTATUS = "comment_familystatus"
        const val COMMENT_USERGROUPROLE = "comment_usergrouprole"
        const val COMMENT_PMSN = "comment_pmsn"
        const val COMMENT_FAMILYPOSTSTATUS = "comment_familypoststatus"
        const val COMMENT_GROUP_ID = "comment_group_id"
    }

    private var replyViewBean: ReplyViewBean? = null//回复对象
    private var owenerReplyViewBean: ReplyViewBean? = null//评论详情回复对象
    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(commentDetailRv, LinearLayoutManager(this)).apply {
            setOnClickListener { view, any ->
                when (view.id) {
                    R.id.replyBtn -> {//回复按钮
                        if (objType != CommConstant.PRAISE_OBJ_TYPE_POST) {
                            if (commentPmsn != 1L) {
                                return@setOnClickListener
                            }
                            barButton?.style = PublishCommentView.Style.EDIT_WITHOUT_MOVIE
                        } else {
                            if (commentPmsn == 1L && familyStatus != CommunityContent.GROUP_JOIN_BLACK_NAME && userGroupRole != GROUP_ROLE_BLACKLIST) {
                                barButton?.updateEditStyle()
                            } else {
                                return@setOnClickListener
                            }
                        }

//                        barButton?.style = PublishCommentView.Style.EDIT_WITHOUT_MOVIE
                        replyViewBean = (any as? CommentReplyBinder)?.bean
                        userName = replyViewBean?.userName.orEmpty()
                        setBarHintText()
                    }
                    R.id.deleteTv -> {//上方评论详情删除
                        mViewModel?.deleteComment(objType, commentId, this)
                    }

                    R.id.likeTv -> {//点赞
                        when (any) {
                            is CommentReplyBinder -> {//回复列表点赞
                                handlePraiseUp(any.bean.isLike, any.bean.replyId, true, any)
                            }
                            is CommentReplyDetailBinder -> {//评论详情上方点赞
                                handlePraiseUp(any.bean.isLike(), any.bean.commentId, false, any)
                            }
                        }
                    }

                }
            }
        }
    }

    private var userName: String = ""
    private var commentId: Long = 0L//评论id
    private var objType: Long = 0L//评论主体
    private var isCommentPositive: Boolean = false//观点id，从pk帖子过来使用
    private var mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
    private var isReplying: Boolean = false//是否在发表评论中
    private var commentPmsn: Long? = 1L//评论权限 1允许任何人 2禁止所有人

    //    帖子使用
    private var isUserBlack: Boolean = false//从帖子详情进来的评论，当前用户是否是黑名单用户
    private var familyStatus: Long = 0L
    private var userGroupRole: Long = 0L
    private var familyPostStatus: Long? = 0L//家族的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论");
    private var groupId: Long? = 0L//家族id
    private var isLoginFromCommentList = isLogin()//从评论列表中进来是否是登录状态

    override fun initVariable() {
        super.initVariable()
        commentId = intent?.getLongExtra(COMMENT_ID, 0L) ?: 0L
        objType = intent?.getLongExtra(COMMENT_OBJECT_TYPE, 0L) ?: 0L
        isCommentPositive = intent?.getBooleanExtra(COMMENT_IS_POSITIVE, false) ?: false
        isUserBlack = intent?.getBooleanExtra(COMMENT_IS_USER_BLACK, false) ?: false
        familyStatus = intent?.getLongExtra(COMMENT_FAMILYSTATUS, 0L) ?: 0L
        userGroupRole = intent?.getLongExtra(COMMENT_USERGROUPROLE, 0L) ?: 0L
        commentPmsn = intent?.getLongExtra(COMMENT_PMSN, 0L) ?: 0L
        familyPostStatus = intent?.getLongExtra(COMMENT_FAMILYPOSTSTATUS, 0L) ?: 0L
        groupId = intent?.getLongExtra(COMMENT_GROUP_ID, 0L) ?: 0L
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initVM(): CommentDetailViewModel = viewModels<CommentDetailViewModel>().value

    override fun initView() {
//        instateView()
        initSmartLayout()
        initBarLayout()

        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                    initData()
                }
            }

        })

        priaseUpObserve()
    }

    private fun initBarLayout() {
        barButton?.apply {
            initStyle()

            action = { type, isSelected ->
                when (type) {
                    BarButtonItem.Type.PRAISE -> {//点赞
                        handlePraiseUp(getSelectedStatusByType(type), commentId, false, this@CommentDetailActivity)
                    }
                    BarButtonItem.Type.SEND -> {//发送评论
                        saveReply(text)
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        keyboard()
                    }
                    BarButtonItem.Type.PHOTO -> {//图片
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos = { photos ->
                            photos.e()
                            if (photos.isNotEmpty()) {
                                commentImgLayout.setPhotoInfo(photos[0])
                            }
                        }
//                        showPhotoAlbumFragment(true,limitedCount = 1L)?.actionUpLoadSuccessPhotos = { photos ->
//                            photos.e()
//                            imageView?.post {
//                                loadCornerImage(imageView,photos[0].url.orEmpty(),109.dp,70.dp,4.dp)
//                            }
//                        }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@CommentDetailActivity.showOrHideSoftInput(this.getEditTextView())
                    }
                }
            }
            editAction = {
                afterLogin {
                    updateEditStyle()
                }
            }

        }
    }

    private fun PublishCommentView.initStyle() {
        isWithoutMovie = true
        style = PublishCommentView.Style.REPLY
        if (objType != CommConstant.PRAISE_OBJ_TYPE_POST) {
            inputEnable(isLogin().not() || commentPmsn == 1L)
        } else {
            inputEnable(commentPmsn == 1L && familyStatus != CommunityContent.GROUP_JOIN_BLACK_NAME && userGroupRole != GROUP_ROLE_BLACKLIST)
        }
        setBarHintText()
    }

    private fun PublishCommentView.updateEditStyle() {
//        未登录 ,如果是从帖子详情品论列表中未登录进入的，，触发登录后直接让可以评论
        if (isLoginFromCommentList && familyPostStatus == JOIN_POST_JOIN_COMMENT) {//加入家族后才能发帖
            //                        判断是否加入了家族
            if (familyStatus == CommunityContent.GROUP_JOIN_SUCCESS) {
                setEditStyle()
            } else if (familyStatus == CommunityContent.GROUP_JOIN_UNDO) {//未加入到家族，显示加入家族对话框
                showToast(R.string.comment_please_join_faimly_to_comment)
            } else if (familyStatus == CommunityContent.GROUP_JOINING) {
                showToast(R.string.comment_please_join_faimly_to_comment)
            }
        } else {////自由评论,管理员发帖 管理员发帖不加入家族也能评论
            setEditStyle()
        }
    }


    /**
     * 显示是否加入到家族的弹框
     */
    private fun showJoinGroupDialog() {
        BaseDialog.Builder(this).setContent(R.string.ugc_is_join_family).setPositiveButton(R.string.community_post_join) { dialog, id ->
            mViewModel?.joinGroup(groupId ?: 0L, dialog)
        }.setNegativeButton(R.string.cancel) { dialog, id ->
            dialog?.dismiss()
        }.create().show()
    }

    /**
     * 更新底部点赞数据
     */
    private fun updateBarLayout(binder: CommentReplyDetailBinder) {
        barButton?.isSelectedByType(BarButtonItem.Type.PRAISE, binder.bean.isLike())
        barButton?.setTipsByType(BarButtonItem.Type.PRAISE, binder.bean.likeCount)
    }

    /**
     * 处理点赞、取消点赞操作
     * @param type 大主题类型
     * @param isCancel true 取消点赞
     * @param isReply 是回复点赞还是评论点赞
     */
    private fun handlePraiseUp(isCancel: Boolean, objectId: Long, isReply: Boolean, extend: Any) {
        val praiseType = CommConstant.getPraiseUpType(objType, if (isReply) CommConstant.COMMENT_PRAISE_ACTION_REPLY else CommConstant.COMMENT_PRAISE_ACTION_COMMENT)
        mViewModel?.praiseUpOrCancel(praiseType, objectId, isCancel, extend)
    }

    private fun getReplyUser(userName: String): String {
        return getString(R.string.comment_reply_to_user_format, userName)
    }

    private fun setBarHintText() {
        barButton?.hintText = getReplyUser(userName)
    }


    /**
     * 提交保存回复
     */
    private fun saveReply(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(commentImgLayout?.getImagePath())) {
            showToast(R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isReplying) {
            return
        }
        mViewModel?.saveReply(objType, replyViewBean?.replyId
                ?: owenerReplyViewBean?.replyId
                ?: 0L, commentId, commentImgLayout?.getImagePath().orEmpty(), text)
    }

    private fun sendMessage(replyId: Long) {
        val index = mBinderList.indexOfFirst { it is CommentReplyBinder }
        var replyContent = if (replyViewBean != null) "@${replyViewBean?.userName.orEmpty()}:${barButton?.text.orEmpty()}" else barButton?.text.orEmpty()
        val commentListBinder = CommentReplyBinder(ReplyViewBean(userId = UserManager.instance.userId,
                userName = UserManager.instance.nickname,
                replyId = replyId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                replyContent = replyContent,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                praiseCount = 0, picUrl = mViewModel?.getUpLoadImageUrl(commentImgLayout?.getImagePath().orEmpty()).orEmpty(),
                isLike = false))
        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()
//        回复数增加
        mBinderList.filterIsInstance<CommentReplyDetailBinder>().forEach {
            it.bean.replyCount++
            it.notifyAdapterSelfChanged()
        }
        mBinderList.removeIf { it is CommentListEmptyBinder }
        if (index < 0) {
            mBinderList.add(commentListBinder)
        } else {
            mBinderList.add(index, commentListBinder)
        }
        mAdapter.notifyAdapterDataSetChanged(mBinderList)
        commentDetailRv?.scrollToPosition(if (index < 0) 0 else index)
        resetInput()

    }

    private fun initSmartLayout() {
        smartRefreshLayout?.apply {
            setEnableLoadMore(true)
            setEnableRefresh(true)

            setOnLoadMoreListener {
                loadReplyListData(true)
            }
            setOnRefreshListener {
                loadReplyListData(false)
            }
        }
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this, false).setLeftClickListener({
            onBackPressed()
        }).setTitle(R.string.comment_detail).create()
    }

    override fun initData() {
//        mBinding?.setVariable(BR.)
        mViewModel?.loadDetail(objType, commentId)

    }

    /**
     * 加载回复列表
     */
    private fun loadReplyListData(isMore: Boolean) {
        mViewModel?.loadReplyListData(objType, commentId, isMore)
    }


    private fun setContentState(@MultiStateView.ViewState type: Int) {
        stateView?.setViewState(type)
        barButton?.visible(type == MultiStateView.VIEW_STATE_CONTENT || type == MultiStateView.VIEW_STATE_EMPTY)
    }

    override fun startObserve() {
        mViewModel?.detailState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
//                    提交成功后需要置空
                    owenerReplyViewBean = CommentViewBean.covertReplyViewBean(bean)
                    userName = this.bean.userName
                    setBarHintText()
                    updateBarLayout(this)
                    mBinderList.add(0, this)
                    mAdapter.notifyAdapterDataSetChanged(mBinderList)
                    loadReplyListData(false)
                }

                netError?.run {
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }

                error?.run {
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
                if (isEmpty) {
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
            }
        })

        mViewModel?.replyState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore()
                smartRefreshLayout?.finishRefresh()
                success?.apply {
                    if (loadMore.not()) {
                        removeAllReplyData()
                    }
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    mBinderList.addAll(this)
                    if (loadMore) {
                        mAdapter.notifyAdapterAdded(mBinderList)
                    } else {
                        var isEmpty = this.any { it is CommentListEmptyBinder }
                        mBinderList.filter { it is CommentReplyDetailBinder }.forEach {
                            (it as CommentReplyDetailBinder).updateShowTriangle(isEmpty.not())
                        }
                        mAdapter.notifyAdapterDataSetChanged(mBinderList)
                    }

                }
            }
        })

//        提交回复
        mViewModel?.saveReplyState?.observe(this, Observer {
            it?.apply {
                isReplying = showLoading
                "isRelying:$isReplying".d()
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    showToast(R.string.comment_component_publish_success)

                    mBinderList.filter { it is CommentReplyDetailBinder }.forEach {
                        (it as CommentReplyDetailBinder).updateShowTriangle(true)
                    }
                    sendMessage(this)
                }
                netError?.showToast()
                error?.showToast()
            }
        })


//        删除评论
        mViewModel?.deleteCommentState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (result as Boolean) {
                        LiveEventBus.get(DELETE_COMMENT).post(
                            com.kotlin.android.app.router.liveevent.event.DeleteCommentState(
                                commentId,
                                isCommentPositive
                            )
                        )
                        finish()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }

        })

        //加入群组
        mViewModel?.joinGroupState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.result.status == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED || this.result.status == CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINING) {//群组成功 或审核中
                        if (this.result.status == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED) {
                            showToast(R.string.ugc_join_success)
                        } else {
                            showToast(this.result.failMsg.orEmpty())
                        }
                        if (this.extend is DialogInterface) {
                            (extend as DialogInterface).dismiss()
                            familyStatus = if (this.result.status ?: 0 == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED) CommunityContent.GROUP_JOIN_SUCCESS else CommunityContent.GROUP_JOINING
                        }
                    } else {
                        showToast(this.result.failMsg.orEmpty())
                    }
                }
                netError?.showToast()
                error?.showToast()

            }
        })


    }

    /**
     * 点赞、取消点赞
     */
    private fun priaseUpObserve() {
        //        点赞、取消点赞
        mViewModel?.praiseUpState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    when (extend) {
                        is CommentReplyBinder -> {////回复列表点赞
                            (extend as? CommentReplyBinder)?.updatePraiseUp()
                        }

                        is CommentReplyDetailBinder -> {//评论详情上方点赞
                            (extend as? CommentReplyDetailBinder)?.let { binder ->
                                binder.updatePraiseUp()
                                updateBarLayout(binder)
                            }
                        }

                        !is MultiTypeBinder<*> -> {//底部点赞
//                            底部样式修改
                            CommonObserver.handlePraiseData(barButton, BarButtonItem.Type.PRAISE, (result as Boolean).not())
//                            更新评论详情binder
                            mBinderList.filterIsInstance<CommentReplyDetailBinder>().forEach { binder ->
                                binder.bean.likeCount = (barButton?.getTipsByType(BarButtonItem.Type.PRAISE)
                                        ?: 0L)
                                binder.bean.userPraised = if (barButton?.getSelectedStatusByType(BarButtonItem.Type.PRAISE) == true) 1L else 0L
                                binder.notifyAdapterSelfChanged()
                            }
                        }
                    }
                }
                netError?.apply {
                    dismissProgressDialog()
                    showToast(this)
                }
                error?.apply {
                    dismissProgressDialog()
                    showToast(this)
                }
            }

        })
    }

    private fun resetInput() {
        replyViewBean = null
        userName = owenerReplyViewBean?.userName.orEmpty()
        setBarHintText()
        if (isShowSoftInput()){
            barButton?.getEditTextView()?.clearFocus()
            barButton?.getEditTextView()?.hideSoftInput()
        }
        barButton?.initStyle()
    }

    override fun onBackPressed() {
        if (barButton?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
            resetInput()
            return
        }
        super.onBackPressed()

    }

    private fun removeAllReplyData() {
        mBinderList.removeAll { it is CommentReplyBinder || it is CommentListEmptyBinder }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }
}