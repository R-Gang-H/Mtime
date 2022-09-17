package com.kotlin.android.ugc.detail.component.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_JOURNAL
import com.kotlin.android.comment.component.*
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.helper.DetailScrollHelper
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_OBJ_TYPE_JOURNAL
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_UNDO
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.user.afterLogin

import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.comment.component.DetailBaseViewModel.Companion.getLongValue
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.ReceiverGroupManager
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.isSelf
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.ugc.detail.component.*
import com.kotlin.android.ugc.detail.component.BR
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.TopicFamilyViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleViewBean
import com.kotlin.android.ugc.detail.component.binder.*
import com.kotlin.android.ugc.detail.component.databinding.ActivityUgcDetailBinding
import com.kotlin.android.ugc.detail.component.listener.IAlbumScrollListener
import com.kotlin.android.ugc.detail.component.observe.WantToSeeObserve
import com.kotlin.android.ugc.detail.component.ui.*
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_ugc_detail.*

/**
 * create by lushan on 2020/8/11
 * description: ugc详情，包括图文和图集详情
 */

@Route(path = RouterActivityPath.UgcDetail.PAGE_UGC_DETAIL_ACTIVITY)
class UgcDetailActivity : BaseVMActivity<UgcDetailViewModel, ActivityUgcDetailBinding>(),
    OnPlayerEventListener, OnErrorEventListener, OnReceiverEventListener {

    private var passType: Long = 0L//传递过来的类型
    private var contentId: Long = 0L//内容id
    private var recId: Long = 0L//记录id
    private var editBtn: CommunityContent.BtnShow? = null//编辑传递记录id
    private var isNewComment = false//是否选中的是最新评论
    private var contentStatus: Long = -1L//创作者内容状态
    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//最热评论
    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(articleRv, LinearLayoutManager(this)).apply {
            setOnClickListener(::handleListAction)
        }
    }

    private var userId: Long = -1L//帖子日志所有者id
    private var postAuth: Long = 0L//群组 发帖和评论的权限
    private var scrollHelper: DetailScrollHelper? = null
    private var needScrollToComment = false//第一次进入加载完数据是否需要滑动到评论处
    private var commonBarBean: UgcCommonBarBean? = null//用户执行reset后恢复底部评论数据
    private var groupBean: TopicFamilyViewBean? = null//群组相关
    private var isCommenting: Boolean = false//是否在发表评论中
    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK -> {
                getShareInfoData(platform)
            }
            SharePlatform.DELETE -> {//删除
                showDeleteDialog()
            }
            SharePlatform.TOP -> {//置顶
                if (mViewModel?.isTop(mBinderList) == true) {
                    mViewModel?.postCancelTop(contentId)
                } else {
                    mViewModel?.postTop(contentId)
                }
            }

            SharePlatform.FINE -> {//加精
                if (mViewModel?.isEssence(mBinderList) == true) {
                    mViewModel?.postCancelEssence(contentId)
                } else {
                    mViewModel?.postEssence(contentId)
                }
            }
            SharePlatform.EDIT -> {
                getProvider(IPublishProvider::class.java) {
                    startEditorActivity(passType,contentId, editBtn?.recId.orZero())
                }
                dismissShareDialog()
            }

        }
    }

    private val loadFinishListener: ((Any?) -> Unit) = {
        firstInAndScrollToComment()
    }

    //    文章视频播放source
    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)

    private var videoId: Long = 0L//文章视频id
    private var videoSourceType = 0L//视频类型 1-预告片 2 自媒体 3-媒资
    private var orientationHelper: OrientationHelper? = null

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        mDataSource = it
        "加载播放地址回调-".e()
        mViewModel?.getVideoPlayUrl(videoId, videoSourceType, "http")
    }

    //    ugc详情+评论binder集合
    private val mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initVM(): UgcDetailViewModel = viewModels<UgcDetailViewModel>().value

    override fun initVariable() {
        super.initVariable()
        passType = intent?.getLongExtra(UGC_DETAIL_TYPE, CONTENT_TYPE_JOURNAL)
            ?: CONTENT_TYPE_JOURNAL
        contentId = intent?.getLongExtra(UGC_DETAIL_CONTENT_ID, 0L).orZero()
        recId = intent?.getLongExtra(UGC_DETAIL_REC_ID, 0L).orZero()
        needScrollToComment = intent?.getBooleanExtra(UGC_DETAIL_NEED_TO_COMMENT, false) ?: false
    }

    override fun initView() {
        initRecyclerView()
        initSmartRefreshLayout()
        initBarButton()
        initVideoView()
        initPraiseAndCollectionState()
        albumTitleView?.topStatusMargin()
        scrollHelper = DetailScrollHelper(articleRv)
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    initData()
                }
            }
        })

        closeIv?.onClick {
            videoView?.pause()
            videoView?.gone()
            closeIv?.gone()
            orientationHelper?.disable()
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

        }
    }


    /**
     * jssdk视频回调
     */
    private val videoListener: ((JsEntity.VideoPlayerEntity.DataBean?) -> Unit)? = {
        it?.apply {
            startVideoPlayer(this)
        }
    }

    /**
     * js回调播放视频
     */
    private fun startVideoPlayer(bean: JsEntity.VideoPlayerEntity.DataBean) {
        videoId = getLongValue(bean.videoID ?: "0")
        videoSourceType = getLongValue(bean.videoType ?: "0")
        mDataSource = MTimeVideoData("", videoId, videoSourceType, 0L)
        videoDataProvider?.updateSourceData(videoId, videoSourceType)
        orientationHelper = OrientationHelper(this)
        runOnUiThread {
            videoView?.apply {
                visible()
                closeIv?.visible()
                setDataProvider(videoDataProvider)
                setDataSource(mDataSource)
                start()
            }
        }


    }

    private fun initVideoView() {
        videoView?.apply {
//            setDataProvider(BaseDataProvider())
            setReceiverGroup(
                ReceiverGroupManager.getBaseReceiverGroup(this@UgcDetailActivity)
            )
            setOnPlayerEventListener(this@UgcDetailActivity)
            setOnErrorEventListener(this@UgcDetailActivity)
            setOnReceiverEventListener(this@UgcDetailActivity)
            setEventHandler(object : OnVideoViewEventHandler() {
            })

            setDataProvider(videoDataProvider)
            handleVideoMarginTop(false)
        }
//        startVideoView()
    }

    private var albumTitleScrollListener: IAlbumScrollListener? = null
    private fun initRecyclerView() {
        albumTitleScrollListener = IAlbumScrollListener(false, albumTitleView)
        albumTitleScrollListener?.run {
            articleRv?.addOnScrollListener(this)
        }
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout?.apply {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setOnLoadMoreListener {
                loadCommentData(true)
            }
        }
    }

    private fun updateCommentCanComment() {
        hotCommentBinderList.filter { it is CommentListBinder }.forEach {
            (it as CommentListBinder).setCommentPmsn(commonBarBean?.commentPmsn)
        }
    }

    private fun initBarButton(ugcCommonBarBean: UgcCommonBarBean) {
        this.commonBarBean = ugcCommonBarBean
        mViewModel?.setTitleBar(passType, ugcCommonBarBean.createUser)//设置标题
        resetInput(CommentHelper.UpdateBarState.INIT)
    }

    /**
     * 三种状态，
     *  INIT,//初始化
     *  ADD,//增加item
     *  DELETE//删除item
     */
    private fun resetInput(
        updateBarState: CommentHelper.UpdateBarState,
        isLongReview: Boolean = false
    ) {
        CommentHelper.resetInput(commonBarBean, barButton, updateBarState)
    }

    private fun initBarButton() {
        barButton?.apply {
            isWithoutMovie = true
            style = PublishCommentView.Style.COMMENT
            inputEnable(true)
            action = { barType, isSelected ->
                when (barType) {
                    BarButtonItem.Type.COMMENT -> {
//                        showToast("评论")
                        scrollHelper?.handleScrollToComment(this@UgcDetailActivity, mBinderList)
                    }
                    BarButtonItem.Type.PRAISE -> {//点赞
                        contentStatus.isPublished {
                            handlePraiseUp(getSelectedStatusByType(barType), this)
                        }
                    }
                    BarButtonItem.Type.DISPRAISE -> {//点踩
                        contentStatus.isPublished {
                            handlePraiseDown(getSelectedStatusByType(barType))
                        }
                    }
                    BarButtonItem.Type.FAVORITE -> {//收藏
                        contentStatus.isPublished {
                            handleCollection(getSelectedStatusByType(barType))
                        }
                    }
                    BarButtonItem.Type.SEND -> {//发送评论
                        saveComment(text)
                    }

                    BarButtonItem.Type.PHOTO -> {//图片
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos =
                            { photos ->
                                photos.e()
                                if (photos.isNotEmpty()) {
                                    commentImgLayout.setPhotoInfo(photos[0])
                                }
                            }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@UgcDetailActivity.showOrHideSoftInput(this.getEditTextView())
                    }
                }
            }
            editAction = {
                afterLogin {
                    contentStatus.isPublished {
                        if (passType == CommConstant.PRAISE_OBJ_TYPE_POST) {//帖子
//                        判断是否加入了家族

                            if (groupBean?.isJoinSuccess() == true) {
                                setEditStyle()
                            } else if (groupBean?.isUnJoinFamily() == true) {//未加入到家族，显示加入家族对话框
                                showJoinGroupDialog()
                            } //其他情况不让点击
                        } else {
                            setEditStyle()
                        }
                    }
                }
            }
        }
    }


    private fun handleVideoMarginTop(isFullScreen: Boolean) {
        val marginLayoutParams = videoView?.layoutParams as? ViewGroup.MarginLayoutParams
        marginLayoutParams?.topMargin = if (isFullScreen) 0 else statusBarHeight + 44.dp
        videoView?.layoutParams = marginLayoutParams

        val closeMarginLayoutParams = closeIv?.layoutParams as? ViewGroup.MarginLayoutParams
        closeMarginLayoutParams?.topMargin = if (isFullScreen) 20.dp else statusBarHeight + 64.dp
        closeIv?.layoutParams = closeMarginLayoutParams
    }

    /**
     * 处理收藏、取消收藏操作
     */
    private fun handleCollection(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.collectionOrCancel(COLLECTION_OBJ_TYPE_JOURNAL, contentId, isCancel, this)
    }

    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    /**
     * 处理点赞、取消点赞操作
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(
                CommConstant.getPraiseUpType(
                    passType,
                    CommConstant.COMMENT_PRAISE_ACTION_COMMENT
                ), commentId, isCancel, extend
            )
        } else {
            mViewModel?.praiseUpOrCancel(passType, contentId, isCancel, extend)
        }
    }

    /**
     * 处理点赞，取消点赞操作
     */
    private fun handlePraiseDown(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(passType, contentId, isCancel, this)
    }

    private fun saveComment(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(commentImgLayout?.getImagePath())) {
            showToast(com.kotlin.android.comment.component.R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isCommenting) return
        mViewModel?.saveComment(
            passType,
            contentId,
            path = commentImgLayout?.getImagePath().orEmpty(),
            content = text
        )
    }

    private fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            com.kotlin.android.comment.component.R.id.likeTv -> {//点赞按钮
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
            com.kotlin.android.comment.component.R.id.deleteTv -> {//删除按钮
                deleteComment(binder)
            }

            R.id.joinFL -> {//加入家族按钮
                joinFamily(binder)
            }
            R.id.movieBtnFl -> {//想看按钮
                handleWann(binder)//处理想看逻辑
            }
            R.id.hotTv -> {//最热按钮
                isNewComment = false
                handleCommentChange()
            }
            R.id.newTv -> {//最新按钮
                isNewComment = true
                handleCommentChange()
            }
        }
    }

    private fun handleCommentChange() {
        mAdapter.notifyAdapterRemoved(mBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }
            .toMutableList()) {
            mBinderList.removeIf { it is CommentListBinder || it is CommentListEmptyBinder }
            hotCommentBinderList.clear()
            loadCommentData(false)
        }
    }

    /**
     * 处理想看逻辑
     */
    private fun handleWann(binder: MultiTypeBinder<*>) {
        if (binder is MovieBinder) {
            if (binder.movieBean.isWanna()) {//想看，此时取消想看
                mViewModel?.wantToSee(
                    binder.movieBean.movieId,
                    DetailBaseViewModel.ACTION_CANCEL,
                    binder
                )
            } else {//取消想看，此时应想看
                mViewModel?.wantToSee(
                    binder.movieBean.movieId,
                    DetailBaseViewModel.ACTION_POSITIVE,
                    binder
                )
            }
        }
    }

    private fun joinFamily(binder: MultiTypeBinder<*>) {
//        加入家族
        if (binder !is FamilyBinder) {
            return
        }
        val familyStatus = binder.bean.familyStatus
        if (familyStatus == GROUP_JOIN_UNDO) {//加入家族
            mViewModel?.joinGroup(binder.bean.familyId, binder)
        } else {
            mViewModel?.exitGroup(binder.bean.familyId, binder)
        }
    }

    /**
     * 删除评论
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(passType, (binder as CommentListBinder).bean.commentId, binder)
    }

    /**
     * 删除内容弹框
     */
    private fun showDeleteDialog() {
        BaseDialog.Builder(this).setContent(R.string.delete_content)
            .setPositiveButton(R.string.ok) { dialog, id ->
//            上个页面传递过来，是日志或帖子
                mViewModel?.deleteContent(passType, contentId)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dismissShareDialog()
                dialog?.dismiss()
            }.create().show()
    }

    /**
     * 显示是否加入到家族的弹框
     */
    private fun showJoinGroupDialog() {
        BaseDialog.Builder(this).setContent(R.string.ugc_is_join_family)
            .setPositiveButton(R.string.ok) { dialog, id ->
                mViewModel?.joinGroup(groupBean?.familyId ?: 0L, dialog)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dialog?.dismiss()
            }.create().show()
    }

    private fun sendMessage(text: String, commentId: Long) {
        //        需要先获取commentId
        val index = mBinderList.indexOfFirst { it is CommentListBinder }
        val commentListBinder = CommentListBinder(
            this, CommentViewBean(
                commentId = commentId,
                userName = UserManager.instance.nickname,
                userId = UserManager.instance.userId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                commentContent = text,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                type = passType,
                objId = contentId,
                commentPic = mViewModel?.getUpLoadImageUrl(
                    commentImgLayout?.getImagePath().orEmpty()
                ).orEmpty()
            )
        )
        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()
        val filter = mBinderList.filter { it is CommentListEmptyBinder }.toMutableList()
        mAdapter.notifyAdapterRemoved(filter) {
            mBinderList.removeAll(filter)
            if (index < 0) {
                mBinderList.add(commentListBinder)
            } else {
                mBinderList.add(index, commentListBinder)
            }
            CommentHelper.addCommentBinder(hotCommentBinderList, commentListBinder)
            updateCommentTitle(false)
            mAdapter.notifyAdapterInsert(index, commentListBinder)
            mBinderList.filter { it is CommentListTitleBinder }.forEach {
                it.notifyAdapterSelfChanged()
            }
            resetInput(CommentHelper.UpdateBarState.ADD)
        }

    }

    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, contentId, passType, isNewComment, isMore)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleView?.setTitleBackground(R.color.color_ffffff)
        albumTitleView?.setTitleBackground(R.color.color_00000000)
        albumTitleView?.setTitleColor(true)
        titleView?.setTitleColor(false)
        val moreClick: (View) -> Unit = {
            contentStatus.contentCanShare(R.string.ugc_detail_is_checking) {
                showShareDialog()
            }
        }
        val backClick: (View) -> Unit = { onBackPressed() }
        val attentionClick: (View, Long) -> Unit = { view, userId ->
            mViewModel?.follow(userId)
        }
        titleView?.setListener(
            moreClick = moreClick,
            back = backClick,
            attentionClick = attentionClick
        )
        titleView?.topStatusMargin()
        albumTitleView?.setListener(
            moreClick = moreClick,
            back = backClick,
            attentionClick = attentionClick
        )
    }

    private fun getShareInfoData(sharePlatform: SharePlatform) {
        mViewModel?.getShareExtendInfo(
            if (passType == CommConstant.PRAISE_OBJ_TYPE_JOURNAL) CommConstant.SHARE_TYPE_JOURNAL else CommConstant.SHARE_TYPE_POST,
            contentId,
            extend = sharePlatform
        )
    }

    override fun initData() {
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mViewModel?.loadUgcDetail(contentId, passType, recId)

    }

    override fun startObserve() {
//        内容相关观察
        detailObserve()
//         评论相关观察
        commentObserve()
//         关注、分享、想看
        wannaAndShareObserve()
//       群组相关观察
        groupObserve()

//      帖子相关观察
        postObserve()

        loginEventObserve()
        //        监听删除评论
        deleteCommentEventObserve()
        //播放地址监听
        playerObserve()
    }

    private fun playerObserve() {
        mViewModel?.videoPlayUrlState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    "加载播放地址：${this}".e()
                    videoDataProvider?.setVideoPlayUrlList(this)
                }

                netError?.apply {
                    videoDataProvider?.setVideoPlayUrlError()
                }
                error?.apply {
                    videoDataProvider?.setVideoPlayUrlError()
                }
            }
        })
    }

    //    评论详情中删除评论
    private fun deleteCommentEventObserve() {
        LiveEventBus.get(
            DELETE_COMMENT,
            com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java
        )
            .observe(this, Observer {
                deleteComment(it?.commentId ?: 0L)
            })
    }

    private fun deleteComment(commentId: Long) {
        CommentHelper.deleteComment(commentId, mBinderList, hotCommentBinderList, mAdapter)
        updateCommentTitle(true)
        resetInput(CommentHelper.UpdateBarState.DELETE)
    }

    private fun wannaAndShareObserve() {
        //        关注
        mViewModel?.followState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(R.string.ugc_follow_success)
                        titleView?.updateFollow(true)
                        albumTitleView?.updateFollow(true)
                    } else {
                        bizMsg?.showToast()
                    }
                }

                error?.showToast()
                netError?.showToast()
            }
        })

//        分享
        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))
        //        想看、取消想看
        mViewModel?.wantToSeeState?.observe(this, WantToSeeObserve(this))
    }

    private fun groupObserve() {
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
                        if (this.extend is FamilyBinder) {
                            val familyBinder = (extend as FamilyBinder)
                            familyBinder.bean.setJoinFamilyStatus(this.result.status ?: 0L)
                            familyBinder.notifyAdapterSelfChanged()
                        } else if (this.extend is DialogInterface) {
                            (extend as DialogInterface).dismiss()
                            mViewModel?.updateJoinFamilyBinder(mBinderList, this.result.status ?: 0)
                        }
                        groupBean?.setJoinFamilyStatus(this.result.status ?: 0L)
                    } else {
                        showToast(this.result.failMsg.orEmpty())
                    }
                }
                netError?.showToast()
                error?.showToast()

            }
        })
        //退出群组
        mViewModel?.exitGroupState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.result.status == 1L) {//退出成功
                        if (this.extend is FamilyBinder) {
                            val familyBinder = (extend as FamilyBinder)
                            familyBinder.bean.familyStatus = GROUP_JOIN_UNDO
                            familyBinder.bean.familyCount--
                            familyBinder.notifyAdapterSelfChanged()
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

    private fun postObserve() {
        //        帖子置顶
        mViewModel?.postTopState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        mViewModel?.updateTopOfUgcTitleBinder(mBinderList, true)
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })

        //        取消置顶
        mViewModel?.postCancelTopState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        mViewModel?.updateTopOfUgcTitleBinder(mBinderList, false)
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })

        //        帖子加精
        mViewModel?.postEssenceState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        mViewModel?.updateEssenceOfUgcTitleBinder(mBinderList, true)
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })

        //        取消帖子加精
        mViewModel?.postCancelEssenceState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        mViewModel?.updateEssenceOfUgcTitleBinder(mBinderList, false)
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })
    }

    private fun commentObserve() {
//        热门评论
        mViewModel?.hotCommentListState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore(500)
                success?.apply {
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    if (hotCommentBinderList.isEmpty()) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mergerBinder()
                    }
                    if (loadMore) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        updateCommentCanComment()
                        mBinderList.addAll(hotCommentBinderList)
                        notifyAdapterData()
                    }
//                    firstInAndScrollToComment()
                }
            }
        })
//最新评论
        mViewModel?.newCommentListState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore(500)
                success?.apply {
                    hotCommentBinderList.addAll(this.commentBinderList)
                    updateCommentCanComment()
                    mBinderList.addAll(hotCommentBinderList)
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    notifyAdapterData()

                }
            }
        })

        //        发表评论
        mViewModel?.saveCommentState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                isCommenting = showLoading
                success?.apply {
                    showToast(R.string.comment_component_publish_success)

                    sendMessage(barButton?.text.orEmpty(), this)
                }

                netError?.apply {
                    showToast(this)
                }
                error?.apply {
                    showToast(this)
                }
            }
        })

        //        删除某条评论
        mViewModel?.deleteCommentState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    dismissProgressDialog()
                    if (extend is MultiTypeBinder<*>) {
                        (extend as? MultiTypeBinder<*>)?.notifyAdapterSelfRemoved()
                        mBinderList.remove(extend)
                        hotCommentBinderList.remove(extend)
                        if (hotCommentBinderList.size == 0) {
                            val emptyBinder = CommentListEmptyBinder()
                            hotCommentBinderList.add(emptyBinder)
                            mBinderList.add(emptyBinder)
                            notifyAdapterData()
                        }
                        updateCommentTitle(true)
                        resetInput(CommentHelper.UpdateBarState.DELETE)
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

    private fun detailObserve() {
        mViewModel?.detailState?.observe(this, Observer {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                }

                success?.apply {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    commonBar?.apply {
                        userId = this.createUser.userId
                        contentStatus = this.createUser.contentStatus
                        this@UgcDetailActivity.editBtn = this.editBtn
                        initBarButton(this)
                    }
                    val binderList = mutableListOf<MultiTypeBinder<*>>()
                    if (bannerData?.isNotEmpty() == true) {
                        binderList.add(
                            UgcBannerImageBinder(
                                bannerData
                                    ?: mutableListOf(), titleData?.title.orEmpty()
                            )
                        )
                    }
//                    if (StatusBarUtils.canControlStatusBarTextColor()) {
//                        StatusBarUtils.translucentStatusBar(this@UgcDetailActivity, bannerData?.isEmpty()
//                                ?: true)
//                    }
                    KeyBoard.assistActivity(
                        this@UgcDetailActivity,
                        bannerData?.isNotEmpty() == true
                    )
                    if (bannerData?.isEmpty() != false) {
                        titleView?.topStatusMargin()
                    } else {
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    }
                    mViewModel?.updateTitleBar(bannerData?.isNotEmpty() == true)
                    albumTitleScrollListener?.isAlbum = bannerData?.isNotEmpty() ?: false
                    if (titleData != null) {
                        titleData?.run {
                            binderList.add(UgcTitleBinder(this))
                        }
                    } else {
                        binderList.add(UgcTitleBinder(UgcTitleViewBean()))
                    }

                    h5Data?.run {
                        binderList.add(
                            UgcWebViewBinder(
                                this,
                                articleRv,
                                loadFinishListener = loadFinishListener,
                                videoListener = videoListener
                            )
                        )
                    }

                    movieViewBean?.run {
                        binderList.add(MovieBinder(this))
                    }
                    groupBean = groupViewBean
                    groupViewBean?.run {
                        binderList.add(FamilyBinder(this))
                    }
                    binderList.add(
                        CommentListTitleBinder(
                            CommentTitleViewBean(
                                commonBar?.commentSupport?.commentCount
                                    ?: 0L
                            )
                        )
                    )

                    mBinderList.clear()
                    hotCommentBinderList.clear()
                    isNewComment = false
                    mBinderList.addAll(0, binderList)
                    mBinderList.addAll(hotCommentBinderList)
                    notifyAdapterData()
                    loadCommentData(false)
//                    firstInAndScrollToComment()
                    mViewModel?.submitContentTrace(passType,contentId.toString(),userId)
                }

                error?.apply {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
                netError?.apply {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                if (isEmpty) {
                    dismissProgressDialog()
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
            }
        })

        //删除内容
        mViewModel?.deleteContent?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(R.string.delete_success)
                        finish()
                    } else {
                        bizMsg?.showToast()
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })
    }

    /**
     * 第一次进入是否要滑动到评论处
     */
    private fun firstInAndScrollToComment() {
        needScrollToComment = scrollHelper?.firstInAndScrollToComment(
            this,
            hotCommentBinderList,
            mBinderList,
            needScrollToComment
        )
            ?: false
    }

    private fun loginEventObserve() {
        LiveEventBus.get(
            LOGIN_STATE,
            com.kotlin.android.app.router.liveevent.event.LoginState::class.java
        ).observe(this, Observer {
            if (it?.isLogin == true) {//登录成功
                hotCommentBinderList.clear()
                mBinderList.clear()
                isNewComment = false
                initData()
            }
        })
    }

    /**
     * 显示分享弹框
     */
    private fun showShareDialog() {
        if (isLogin() && isSelf(userId)) {//登录了且是自己的日志，就显示删除按钮
            if (editBtn != null) {
                showShareDialog(
                    null,
                    ShareFragment.LaunchMode.ADVANCED,
                    SharePlatform.COPY_LINK,
                    SharePlatform.EDIT,
                    SharePlatform.DELETE,
                    event = shareAction
                )
            } else {
                showShareDialog(
                    null,
                    ShareFragment.LaunchMode.ADVANCED,
                    SharePlatform.COPY_LINK,
                    SharePlatform.DELETE,
                    event = shareAction
                )
            }

        } else {
            showShareDialog(
                null,
                ShareFragment.LaunchMode.ADVANCED,
                SharePlatform.COPY_LINK,
                event = shareAction
            )
        }
    }

    /**
     * 更新标题数量
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        CommentHelper.updateCommentTitleList(mBinderList, isDelete)
    }

    private fun initPraiseAndCollectionState() {
        //        点赞、取消点赞
        mViewModel?.praiseUpState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_PRAISE_UP,
                barButton,
                BarButtonItem.Type.PRAISE
            )
        )

        //        点踩、取消点踩
        mViewModel?.praiseDownState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_PRAISE_DOWN,
                barButton,
                BarButtonItem.Type.DISPRAISE
            )
        )

        //收藏、取消收藏
        mViewModel?.collectionState?.observe(
            this,
            CommonObserver(
                this,
                CommonObserver.TYPE_COLLECTIN,
                barButton,
                BarButtonItem.Type.FAVORITE
            )
        )
    }

    private fun mergerBinder() {
        if (isContainsDetailBinder()) {
            mBinderList.addAll(hotCommentBinderList)
        }
        notifyAdapterData()
    }

    /**
     * 是否已经加载过详情
     */
    private fun isContainsDetailBinder(): Boolean {
        return mBinderList.any { it is UgcBannerImageBinder || it is UgcTitleBinder || it is UgcWebViewBinder || it is MovieBinder }
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
        val show = state == MultiStateView.VIEW_STATE_CONTENT
        barButton?.visible(show)
        mBinding?.titleView?.setMoreVisible(show)
        mBinding?.albumTitleView?.setMoreVisible(show)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (barButton?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
            resetInput(CommentHelper.UpdateBarState.INIT)
            return
        }
        super.onBackPressed()
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        "eventCode:$eventCode".e()
        when (eventCode) {

            DataInter.ReceiverEvent.EVENT_REQUEST_BACK -> onBackPressed()
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> {//屏幕切换状态
                orientationHelper?.toggleScreen()
            }
//            DataInter.ReceiverEvent.EVENT_CODE_SHARE -> {//分享
//
//            }

        }
    }

    private var isFullScreen = false
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        hideSoftInput()
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = false
            PlayerHelper.portraitMatchWidth_16_9(this, videoView, null)
//            恢复状态栏颜色
            immersive().statusBarColor(
                albumTitleView?.getTitleBackgroundColor() ?: getColor(R.color.transparent)
            )
            notifyAdapterData()
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//状态栏设置为透明色
            isFullScreen = true
            PlayerHelper.landscapeMatchWidthHeight(this, videoView, null)
            immersive().statusBarColor(getColor(R.color.transparent))
        }
        handleVideoMarginTop(isFullScreen)
    }

    private fun notifyAdapterData() {
        mAdapter.notifyAdapterDataSetChanged(mBinderList, false)
    }

    override fun onPause() {
        videoView?.pause()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        orientationHelper?.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationHelper?.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationHelper?.destroy()
        videoView?.stopPlayback()
    }
}