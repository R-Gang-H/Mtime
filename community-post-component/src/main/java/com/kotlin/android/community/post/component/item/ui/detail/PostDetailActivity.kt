package com.kotlin.android.community.post.component.item.ui.detail

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
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOINING
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_BLACK_NAME
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.bonus.scene.component.postJoinFamily
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.DetailBaseViewModel
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
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ActivityPostDetailBinding
import com.kotlin.android.community.post.component.item.adapter.PkCommentBinder
import com.kotlin.android.community.post.component.item.adapter.PkVoteDetailBinder
import com.kotlin.android.community.post.component.item.bean.PKComentViewBean
import com.kotlin.android.community.post.component.item.bean.PKStatusViewBean
import com.kotlin.android.community.post.component.item.observe.PkObserve
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.ReceiverGroupManager
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.isSelf
import com.kotlin.android.router.liveevent.COLLECTION_OR_CANCEL
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.ugc.detail.component.BR
import com.kotlin.android.ugc.detail.component.bean.TopicFamilyViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleViewBean
import com.kotlin.android.ugc.detail.component.binder.*
import com.kotlin.android.ugc.detail.component.contentCanEdit
import com.kotlin.android.ugc.detail.component.contentCanShare
import com.kotlin.android.ugc.detail.component.isPublished
import com.kotlin.android.ugc.detail.component.listener.IAlbumScrollListener
import com.kotlin.android.ugc.detail.component.observe.WantToSeeObserve
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_NEED_TO_COMMENT
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_REC_ID
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_post_detail.*

/**
 * create by lushan on 2020/11/2
 * description:帖子详情，包括普通帖子和pk帖子
 */
@Route(path = RouterActivityPath.Post.PAGE_POST_DETAIL_ACTIVITY)
class PostDetailActivity : BaseVMActivity<PostDetailViewModel, ActivityPostDetailBinding>(),
    OnPlayerEventListener, OnErrorEventListener, OnReceiverEventListener {
    companion object {
        const val POST_CONTENT_ID = "post_content_id"
        const val POST_TYPE = "post_type"
    }

    private var passType: Long = 0L//传递过来的类型
    private var contentId: Long = 0L//内容id
    private var recId: Long = 0L//记录id
    private var editBtn: CommunityContent.BtnShow? = null//编辑传递记录id
    private var isNewComment = false//是否选中的是最新评论
    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//最热评论
    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(articleRv, LinearLayoutManager(this)).apply {
            setOnClickListener(::handleListAction)
        }
    }

    private var positiveId = -1L//支持方id
    private var nagivativeId = -1L//反对方id
    private var positiveHasNoMore: Boolean = false//支持方没有下页数据
    private var nagivativeHasMore: Boolean = false//反对方没有下页数据
    private var userVotedId = 0L//用户选择的投票方


    private var userId: Long = -1L//帖子所有者id
    private var contentStatus: Long = -1L//创作者内容状态
    private var scrollHelper: DetailScrollHelper? = null
    private var needScrollToComment = false//第一次进入加载完数据是否需要滑动到评论处
    private var commonBarBean: UgcCommonBarBean? = null//用户执行reset后恢复底部评论数据
    private var groupBean: TopicFamilyViewBean? = null//群组相关
    private var isPk = false//是否是pk帖子
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
                mViewModel?.postTop(contentId)
            }
            SharePlatform.TOP_CANCEL -> {//取消置顶
                mViewModel?.postCancelTop(contentId)
            }
            SharePlatform.FINE_CANCEL -> {//取消加精
                mViewModel?.postCancelEssence(contentId)
            }

            SharePlatform.FINE -> {//加精
                mViewModel?.postEssence(contentId)
            }

            SharePlatform.EDIT -> {//编辑
                getProvider(IPublishProvider::class.java) {
                    startEditorActivity(passType, contentId, editBtn?.recId.orZero())
                }
                dismissShareDialog()
            }

        }
    }
    private var orientationHelper: OrientationHelper? = null

    //    文章视频播放source
    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        mDataSource = it
        "加载播放地址回调-".e()
        mViewModel?.getVideoPlayUrl(videoId, videoSourceType, "http")
    }

    private var videoId: Long = 0L//文章视频id
    private var videoSourceType = 0L//视频类型 1-预告片 2 自媒体 3-媒资

    private val loadFinishListener: ((Any?) -> Unit) = {
        firstInAndScrollToComment()
    }

    /**
     * jssdk视频回调
     */
    private val videoListener: ((JsEntity.VideoPlayerEntity.DataBean?) -> Unit)? = {
        it?.apply {
            startVideoPlayer(this)
        }
    }

    private val pkListener: ((View, PKComentViewBean, MultiTypeBinder<*>) -> Unit)? =
        { view, pkComentViewBean, binder ->
            when (view.id) {
                R.id.praiseTv -> {//pk评论点赞
                    handlePraiseUp(
                        CommConstant.PRAISE_OBJ_TYPE_POST_COMMENT,
                        pkComentViewBean.commentId,
                        pkComentViewBean.isPariseUp(),
                        pkComentViewBean
                    )
                }
                R.id.unPraiseTv -> {//pk评论点踩
                    handlePraiseDown(
                        CommConstant.PRAISE_OBJ_TYPE_POST_COMMENT,
                        pkComentViewBean.commentId,
                        pkComentViewBean.isPraiseDown(),
                        pkComentViewBean
                    )
                }
            }

        }

    //    ugc详情+评论binder集合
    private val mBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initVM(): PostDetailViewModel = viewModels<PostDetailViewModel>().value

    override fun initVariable() {
        super.initVariable()
        passType = intent?.getLongExtra(POST_TYPE, CONTENT_TYPE_POST)
            ?: CONTENT_TYPE_POST
        contentId = intent?.getLongExtra(POST_CONTENT_ID, 0L).orZero()
        recId = intent?.getLongExtra(UGC_DETAIL_REC_ID, 0L).orZero()

        needScrollToComment = intent?.getBooleanExtra(UGC_DETAIL_NEED_TO_COMMENT, false) ?: false
    }

    override fun initView() {
        initRecyclerView()
        initSmartRefreshLayout()
        initBarButton()
        initVideoView()
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

    private fun initVideoView() {
        videoView?.apply {
//            setDataProvider(BaseDataProvider())
            setReceiverGroup(
                ReceiverGroupManager.getBaseReceiverGroup(this@PostDetailActivity)
            )
            setOnPlayerEventListener(this@PostDetailActivity)
            setOnErrorEventListener(this@PostDetailActivity)
            setOnReceiverEventListener(this@PostDetailActivity)
            setEventHandler(object : OnVideoViewEventHandler() {
            })

            setDataProvider(videoDataProvider)
            handleVideoMarginTop(false)
        }
//        startVideoView()
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
     * js回调播放视频
     */
    private fun startVideoPlayer(bean: JsEntity.VideoPlayerEntity.DataBean) {
        videoId = DetailBaseViewModel.getLongValue(bean.videoID ?: "0")
        videoSourceType = DetailBaseViewModel.getLongValue(bean.videoType ?: "0")
        mDataSource = MTimeVideoData("", videoId, videoSourceType, 0L)
        videoDataProvider?.updateSourceData(videoId, videoSourceType)
        orientationHelper = OrientationHelper(this)
        orientationHelper?.sensorEnable(false)
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

    private fun initBarButton(ugcCommonBarBean: UgcCommonBarBean) {
        this.commonBarBean = ugcCommonBarBean
        userVotedId = ugcCommonBarBean.commentSupport.userVotedId
        mViewModel?.setTitleBar(passType, ugcCommonBarBean.createUser)//设置标题
        //        获取双方观点id
        val vote = ugcCommonBarBean.commentSupport.vote
        vote?.apply {
            if (opts?.isNotEmpty() == true) {
                positiveId = opts?.get(0)?.optId ?: -1L
                if ((opts?.size ?: 0) >= 2) {
                    nagivativeId = opts?.get(1)?.optId ?: -1L
                }
            }
        }
        resetInput(CommentHelper.UpdateBarState.INIT)
    }

    /**
     * 三种状态，
     *  INIT,//初始化
     *  ADD,//增加item
     *  DELETE//删除item
     */
    private fun resetInput(updateBarState: CommentHelper.UpdateBarState) {
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
                        scrollHelper?.handleScrollToComment(
                            this@PostDetailActivity,
                            mBinderList,
                            isPk
                        )
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
//                        if (isPk.not()) {
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos =
                            { photos ->
                                photos.e()
                                if (photos.isNotEmpty()) {
                                    commentImgLayout.setPhotoInfo(photos[0])
                                }
                            }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@PostDetailActivity.showOrHideSoftInput(this.getEditTextView())
                    }
                }
            }
            editAction = {
                afterLogin {
                    contentStatus.isPublished {
                        if (groupBean?.familyPostStatus == JOIN_POST_JOIN_COMMENT) {//加入家族后才能发帖
                            //                        判断是否加入了家族
                            if (groupBean?.isJoinSuccess() == true) {
                                setEditStyleByPk()
                            } else if (groupBean?.isUnJoinFamily() == true) {//未加入到家族，显示加入家族对话框
                                showJoinGroupDialog()
                            } else if (groupBean?.familyStatus == GROUP_JOINING) {
                                showToast(R.string.comment_please_join_faimly_to_comment)
                            }
                        } else {////自由评论,管理员发帖 管理员发帖不加入家族也能评论
                            setEditStyleByPk()
                        }

                    }

                }

            }

        }
    }

    private fun setEditStyleByPk() {
        if (isPk) {//pk帖子需要先判断是否
            if (userVotedId == 0L) {
                showToast(R.string.community_please_select_option_to_vote)
            } else {
                barButton?.setEditStyle()
            }
        } else {
            barButton?.setEditStyle()
        }
    }

    /**
     * 处理收藏、取消收藏操作
     */
    private fun handleCollection(isCancel: Boolean) {
        showInteractiveDialog()
        mViewModel?.collectionOrCancel(
            CommConstant.COLLECTION_OBJ_TYPE_POST,
            contentId,
            isCancel,
            this
        )
        LiveEventBus.get(COLLECTION_OR_CANCEL).post(
            com.kotlin.android.app.router.liveevent.event.CollectionState(
                CommConstant.COLLECTION_TYPE_POST
            )
        )
    }

    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    /**
     * 处理pk点赞、取消点赞操作
     */
    private fun handlePraiseUp(type: Long, contentId: Long, isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        mViewModel?.praiseUpOrCancel(type, contentId, isCancel, extend)
    }

    /**
     * 处理pk点赞，取消点赞操作
     */
    private fun handlePraiseDown(type: Long, contentId: Long, isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(type, contentId, isCancel, extend)
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
        if (isPk) {
            if (userVotedId == 0L) {
                return
            }
            mViewModel?.saveComment(
                passType,
                contentId,
                optId = userVotedId,
                path = commentImgLayout?.getImagePath().orEmpty(),
                content = text
            )
        } else {
            mViewModel?.saveComment(
                passType,
                contentId,
                path = commentImgLayout?.getImagePath().orEmpty(),
                content = text
            )
        }
    }

    private fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            com.kotlin.android.comment.component.R.id.likeTv -> {//点赞按钮,普通帖子点赞
                val isCancel = (binder as CommentListBinder).bean.isLike()
                handlePraiseUp(isCancel, binder)
            }
            com.kotlin.android.comment.component.R.id.deleteTv -> {//删除按钮
                deleteComment(binder)
            }

            com.kotlin.android.ugc.detail.component.R.id.joinFL -> {//加入家族按钮
                joinFamily(binder)
            }
            com.kotlin.android.ugc.detail.component.R.id.movieBtnFl -> {//想看按钮
                handleWann(binder)//处理想看逻辑
            }
            R.id.positiveTv -> {//支持
                if (contentStatus.isPublished().not()) {
                    showToast(R.string.community_post_is_checking)
                    return
                }
                showInteractiveDialog()
                mViewModel?.vote(passType, contentId, positiveId, positiveId)
            }
            R.id.navigationTv -> {//反对
                if (contentStatus.isPublished().not()) {
                    showToast(R.string.community_post_is_checking)
                    return
                }
                showInteractiveDialog()
                mViewModel?.vote(passType, contentId, nagivativeId, nagivativeId)
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
            "切换评论列表：$mBinderList".d()
//            mAdapter.notifyAdapterChanged(mBinderList)
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
        if (familyStatus == CommunityContent.GROUP_JOIN_UNDO) {//加入家族
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
        BaseDialog.Builder(this)
            .setContent(com.kotlin.android.ugc.detail.component.R.string.delete_content)
            .setPositiveButton(com.kotlin.android.ugc.detail.component.R.string.ok) { dialog, id ->
//            上个页面传递过来，是日志或帖子
                mViewModel?.deleteContent(passType, contentId)
            }
            .setNegativeButton(com.kotlin.android.ugc.detail.component.R.string.cancel) { dialog, id ->
                dismissShareDialog()
                dialog?.dismiss()
            }.create().show()
    }

    /**
     * 显示是否加入到家族的弹框
     */
    private fun showJoinGroupDialog() {
        BaseDialog.Builder(this).setContent(R.string.ugc_is_join_family)
            .setPositiveButton(R.string.community_post_join) { dialog, id ->
                mViewModel?.joinGroup(groupBean?.familyId ?: 0L, dialog)
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dialog?.dismiss()
            }.create().show()
    }

    /**
     * 评论列表中添加评论
     */
    private fun sendPkMessage(text: String, commentId: Long) {
        //        需要先获取commentId
        val commentViewBean = PKComentViewBean(
            commentId = commentId,
            userNickName = UserManager.instance.nickname,
            userId = UserManager.instance.userId,
            comment = text,
            userHeadPic = UserManager.instance.userAvatar.orEmpty(),
            isCommentPositive = userVotedId == positiveId
        )
        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()
        mViewModel?.addPkComment(commentViewBean, userVotedId == positiveId, true, mBinderList)

        resetInput(CommentHelper.UpdateBarState.ADD)
    }

    private fun isBlackUser(): Boolean {
        return groupBean?.familyStatus == GROUP_JOIN_BLACK_NAME || groupBean?.userGroupRole == GROUP_ROLE_BLACKLIST
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
        commentListBinder.setBlackUser(
            groupBean?.familyStatus ?: 0L, groupBean?.userGroupRole
                ?: 0L, groupBean?.familyPostStatus ?: 0L, groupBean?.familyId
                ?: 0L, commonBarBean?.commentPmsn
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
//            reviewRV?.scrollToPosition(if (index < 0) 0 else index)
            resetInput(CommentHelper.UpdateBarState.ADD)
        }

    }

    private fun loadCommentData(isMore: Boolean) {
        if (isPk) {//pk帖子
            //        isNewComment 为true 代表反对方  false代表支持方  uiModle 对应new 和hot
            if (positiveId != -1L && positiveHasNoMore.not()) {
                mViewModel?.loadCommentList(
                    this,
                    contentId,
                    passType,
                    false,
                    isMore,
                    positiveId,
                    null
                )
            }
            if (nagivativeId != -1L && nagivativeHasMore.not()) {
                mViewModel?.loadCommentList(
                    this,
                    contentId,
                    passType,
                    true,
                    isMore,
                    nagivativeId,
                    null
                )
            }
        } else {//普通帖子
            mViewModel?.loadCommentList(this, contentId, passType, isNewComment, isMore)
        }
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleView?.setTitleBackground(com.kotlin.android.ugc.detail.component.R.color.color_ffffff)
        albumTitleView?.setTitleBackground(com.kotlin.android.ugc.detail.component.R.color.color_00000000)
        albumTitleView?.setTitleColor(true)
        titleView?.setTitleColor(false)
        val moreClick: (View) -> Unit = {
            contentStatus.contentCanShare(R.string.community_post_is_checking) {
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

    private fun getShareInfoData(platform: SharePlatform) {
        mViewModel?.getShareExtendInfo(
            if (passType == CommConstant.PRAISE_OBJ_TYPE_JOURNAL) CommConstant.SHARE_TYPE_JOURNAL else CommConstant.SHARE_TYPE_POST,
            contentId,
            extend = platform
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
        //        视频播放地址
        playerObserve()

        loginEventObserve()
        //        监听删除评论
        deleteCommentEventObserve()
    }

    //    评论详情中删除评论
    private fun deleteCommentEventObserve() {
        LiveEventBus.get(
            DELETE_COMMENT,
            com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java
        )
            .observe(this, Observer {
                if (isPk.not()) {
                    deleteComment(it?.commentId ?: 0L)
                } else {
                    deletePkComment(it?.commentId ?: 0L)
                }
            })
    }

    private fun deletePkComment(comentId: Long) {
        mBinderList.filter { it is PkCommentBinder }.forEach {
            (it as PkCommentBinder).deleteComment(comentId)
        }

    }

    private fun deleteComment(commentId: Long) {
        CommentHelper.deleteComment(commentId, mBinderList, hotCommentBinderList, mAdapter)
        updateCommentTitle(true)
        resetInput(CommentHelper.UpdateBarState.DELETE)
    }

    private fun wannaAndShareObserve() {
        //        投票结果
        mViewModel?.voteState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    val commBizCodeResult = this.result as CommBizCodeResult
                    if (commBizCodeResult.isSuccess()) {//投票成功
                        if (this.extend is Long) {//观点id
                            userVotedId = this.extend as Long
//                      更新观点布局
                            mViewModel?.updatePkStatusData(
                                positiveId == (this.extend as Long),
                                mBinderList
                            )
                        }
                    }
                }
                netError?.showToast()
                error?.showToast()
            }
        })

        //        关注
        mViewModel?.followState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (isSuccess()) {
                        showToast(com.kotlin.android.ugc.detail.component.R.string.ugc_follow_success)
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

//        //        分享
//        mViewModel?.shareUIState?.observe(this, Observer {
//            it?.apply {
//                showOrHideProgressDialog(showLoading)
//                success?.apply {
//                    showShareDialog()
//                }
//                netError?.showToast()
//                error?.showToast()
//            }
//        })
        //        想看、取消想看
        mViewModel?.wantToSeeState?.observe(this, WantToSeeObserve(this))
        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))
    }

    private fun updateCommentGroup() {
        if (isPk) {
            mBinderList.filter { it is PkCommentBinder }.forEach {
                (it as PkCommentBinder).updateGroup(
                    groupBean?.familyStatus
                        ?: 0L, groupBean?.userGroupRole ?: 0L, groupBean?.familyPostStatus
                        ?: 0L, groupBean?.familyId ?: 0L, commonBarBean?.commentPmsn
                )
            }
        } else {
            hotCommentBinderList.filter { binder -> binder is CommentListBinder }
                ?.forEach { binder ->
                    (binder as CommentListBinder).setBlackUser(
                        groupBean?.familyStatus
                            ?: 0L, groupBean?.userGroupRole ?: 0L, groupBean?.familyPostStatus
                            ?: 0L, groupBean?.familyId ?: 0L, commonBarBean?.commentPmsn
                    )
                }
        }
    }

    private fun groupObserve() {
        //加入群组
        mViewModel?.joinGroupState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.result.status == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED || this.result.status == CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINING) {//群组成功 或审核中
                        if (this.result.status == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED) {
                            showToast(com.kotlin.android.ugc.detail.component.R.string.ugc_join_success)
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
                        postJoinFamily()
                        groupBean?.setJoinFamilyStatus(this.result.status ?: 0L)
                        updateCommentGroup()
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
                            familyBinder.bean.familyStatus = CommunityContent.GROUP_JOIN_UNDO
                            familyBinder.bean.familyCount--
                            familyBinder.notifyAdapterSelfChanged()
                        }
                        groupBean?.setExitFamilyStatus(this.result.status ?: 0L)
                        updateCommentGroup()
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
                        dismissShareDialog()
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
                        dismissShareDialog()
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
                        dismissShareDialog()
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
                        dismissShareDialog()
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

    private fun addPkComment(
        list: MutableList<MultiTypeBinder<*>>,
        positive: Boolean,
        familyStatus: Long,
        userGroupRole: Long,
        familyPostStatus: Long,
        groupId: Long,
        commentPmsn: Long?
    ) {
        if (list.isEmpty()) return
        mBinderList.filter { it is PkCommentBinder }.forEach {
            (it as PkCommentBinder).addComment(
                list,
                positive,
                familyStatus,
                userGroupRole,
                familyPostStatus,
                groupId,
                commentPmsn
            )
        }
    }

    private fun commentObserve() {
//        热门评论 如果是pk帖子则为支持方评论
        mViewModel?.hotCommentListState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore(500)
                success?.apply {
                    if (isPk) {
                        positiveHasNoMore = noMoreData
                        addPkComment(
                            this.commentBinderList, true, groupBean?.familyStatus
                                ?: 0L, groupBean?.userGroupRole ?: 0L, groupBean?.familyPostStatus
                                ?: 0L, groupBean?.familyId ?: 0L, commonBarBean?.commentPmsn
                        )
                        smartRefreshLayout?.setNoMoreData(positiveHasNoMore && nagivativeHasMore)
                    } else {
                        smartRefreshLayout?.setNoMoreData(noMoreData)
                        if (hotCommentBinderList.isEmpty()) {
                            hotCommentBinderList.addAll(this.commentBinderList)
                            hotCommentBinderList.filter { binder -> binder is CommentListBinder }
                                ?.forEach { binder ->
                                    (binder as CommentListBinder).setBlackUser(
                                        groupBean?.familyStatus
                                            ?: 0L, groupBean?.userGroupRole
                                            ?: 0L, groupBean?.familyPostStatus
                                            ?: 0L, groupBean?.familyId
                                            ?: 0L, commonBarBean?.commentPmsn
                                    )
                                }
                            mergerBinder()
                        }
                        if (loadMore) {
                            hotCommentBinderList.addAll(this.commentBinderList)
                            hotCommentBinderList.filter { binder -> binder is CommentListBinder }
                                ?.forEach { binder ->
                                    (binder as CommentListBinder).setBlackUser(
                                        groupBean?.familyStatus
                                            ?: 0L, groupBean?.userGroupRole
                                            ?: 0L, groupBean?.familyPostStatus
                                            ?: 0L, groupBean?.familyId
                                            ?: 0L, commonBarBean?.commentPmsn
                                    )
                                }
                            mBinderList.addAll(hotCommentBinderList)
                            notifyAdapterData()
                        }
//                        firstInAndScrollToComment()
                    }
                }

            }
        })
//最新评论 如果是pk帖子则为反对方评论
        mViewModel?.newCommentListState?.observe(this, Observer {
            it?.apply {
                smartRefreshLayout?.finishLoadMore(500)
                success?.apply {
                    if (isPk) {
                        nagivativeHasMore = noMoreData
                        addPkComment(
                            this.commentBinderList, false, groupBean?.familyStatus
                                ?: 0L, groupBean?.userGroupRole ?: 0L, groupBean?.familyPostStatus
                                ?: 0L, groupBean?.familyId ?: 0L, commonBarBean?.commentPmsn
                        )
                        smartRefreshLayout?.setNoMoreData(positiveHasNoMore && nagivativeHasMore)
                    } else {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        hotCommentBinderList.filter { binder -> binder is CommentListBinder }
                            ?.forEach { binder ->
                                (binder as CommentListBinder).setBlackUser(
                                    groupBean?.familyStatus
                                        ?: 0L,
                                    groupBean?.userGroupRole
                                        ?: 0L,
                                    groupBean?.familyPostStatus ?: 0L,
                                    groupBean?.familyId
                                        ?: 0L,
                                    commonBarBean?.commentPmsn
                                )
                            }
                        mBinderList.addAll(hotCommentBinderList)
                        smartRefreshLayout?.setNoMoreData(noMoreData)
                        notifyAdapterData()

                    }
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

                    if (isPk) {
                        sendPkMessage(barButton?.text.orEmpty(), this)
                    } else {
                        sendMessage(barButton?.text.orEmpty(), this)
                    }
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

                            updateCommentTitle(true)
                            resetInput(CommentHelper.UpdateBarState.DELETE)
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
                        this@PostDetailActivity.editBtn = this.editBtn
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
//                        StatusBarUtils.translucentStatusBar(this@PostDetailActivity, bannerData?.isEmpty()
//                                ?: true)
//                    }
                    KeyBoard.assistActivity(
                        this@PostDetailActivity,
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

//                    判断是否是pk帖子
                    commonBar?.apply {
                        isPk = this.commentSupport?.vote != null
                        if (isPk) {
//                        需要添加pk的控件
                            binderList.add(PkVoteDetailBinder(PKStatusViewBean.create(commentSupport)))
                            binderList.add(
                                PkCommentBinder(
                                    mutableListOf(),
                                    mutableListOf(),
                                    pkListener
                                )
                            )
                        }
                    }
                    if (isPk) {
                        initPkPraiseAndCollectionState()
                    } else {
                        initPraiseAndCollectionState()
                    }
                    if (isPk.not()) {
                        binderList.add(
                            CommentListTitleBinder(
                                CommentTitleViewBean(
                                    commonBar?.commentSupport?.commentCount
                                        ?: 0L
                                )
                            )
                        )
                    }

                    mBinderList.clear()
                    hotCommentBinderList.clear()
                    isNewComment = false
//                    加载评论

                    mBinderList.addAll(0, binderList)
//                    mBinderList.addAll(hotCommentBinderList)
                    notifyAdapterData()
//                    firstInAndScrollToComment()
                    loadCommentData(false)
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
                        showToast(com.kotlin.android.ugc.detail.component.R.string.delete_success)
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
            needScrollToComment,
            isPk
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
                positiveHasNoMore = false
                nagivativeHasMore = false
                initData()
            }
        })
    }

    /**
     * 显示分享弹框
     */
    private fun showShareDialog() {
        if (isLogin()) {
            if (groupBean?.userGroupRole == CommunityContent.Group.USER_GROUP_ROLE_OWNER || groupBean?.userGroupRole == CommunityContent.Group.USER_GROUP_ROLE_ADMINISTRATOR) {//族长和管理员需要展示制定、加精、删除按钮
                if (isSelf(userId)){
                    showShareDialog(
                        null,
                        ShareFragment.LaunchMode.ADVANCED,
                        SharePlatform.COPY_LINK,
                        if (mViewModel?.isTop(mBinderList) == true) SharePlatform.TOP_CANCEL else SharePlatform.TOP,
                        if (mViewModel?.isEssence(mBinderList) == true) SharePlatform.FINE_CANCEL else SharePlatform.FINE,
                        SharePlatform.EDIT,
                        SharePlatform.DELETE,
                        event = shareAction
                    )
                }else{
                    showShareDialog(
                        null,
                        ShareFragment.LaunchMode.ADVANCED,
                        SharePlatform.COPY_LINK,
                        if (mViewModel?.isTop(mBinderList) == true) SharePlatform.TOP_CANCEL else SharePlatform.TOP,
                        if (mViewModel?.isEssence(mBinderList) == true) SharePlatform.FINE_CANCEL else SharePlatform.FINE,
                        SharePlatform.DELETE,
                        event = shareAction
                    )
                }

            } else if (isSelf(userId)) {
                if (editBtn!=null) {
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

    /**
     * pk帖子点赞
     */
    private fun initPkPraiseAndCollectionState() {
        mViewModel?.collectionState?.observe(
            this,
            PkObserve(
                this,
                CommonObserver.TYPE_COLLECTIN,
                barButton,
                BarButtonItem.Type.FAVORITE,
                null,
                mBinderList
            )
        )
        mViewModel?.praiseUpState?.observe(
            this,
            PkObserve(
                this,
                CommonObserver.TYPE_PRAISE_UP,
                barButton,
                BarButtonItem.Type.PRAISE,
                null,
                mBinderList
            )
        )
        mViewModel?.praiseDownState?.observe(
            this,
            PkObserve(
                this,
                CommonObserver.TYPE_PRAISE_DOWN,
                barButton,
                BarButtonItem.Type.DISPRAISE,
                null,
                mBinderList
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
        val isShow = state == MultiStateView.VIEW_STATE_CONTENT
        barButton?.visible(isShow)
        mBinding?.albumTitleView?.setMoreVisible(isShow)
        mBinding?.titleView?.setMoreVisible(isShow)
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
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationHelper?.destroy()
        videoView?.stopPlayback()
    }
}