package com.kotlin.android.video.component.ui.detail

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContent.Companion.TYPE_VIDEO_DETIAL
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.comment.component.DELETE_COMMENT
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.helper.DetailScrollHelper
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.keyboard.showOrHideSoftInput
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.GlobalDimensionExt.getDigitsCurrentCityId
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.splayer.ISPayer
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.observer.ShareObserver
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.video.component.R
import com.kotlin.android.video.component.adapter.VIDEO_DETAIL_HAD_WANNA
import com.kotlin.android.video.component.adapter.VIDEO_DETAIL_UN_ATTITUDE
import com.kotlin.android.video.component.binder.VideoDetailTitleBinder
import com.kotlin.android.video.component.databinding.ActivityVideoDetailBinding
import com.kotlin.android.video.component.viewbean.PreVideoCommentTitleBean
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.activity_video_detail.*

/**
 * 预告片详情
 *
 */
@Route(path = RouterActivityPath.Video.PAGE_VIDEO_PRE_VIDEO_ACTIVITY)
class VideoDetailActivity : BaseVMActivity<VideoDetailViewModel, ActivityVideoDetailBinding>(), OnReceiverEventListener {
    companion object {
        const val KEY_PRE_VIDEO_ID = "video_id"
    }

    private var videoId: Long = 0L//预告片视频id
    private lateinit var adapter: MultiTypeAdapter

    private val type = TYPE_VIDEO_DETIAL//评论主体类型，视频详情
    private var videoSourceType = 0L//视频类型 1-预告片 2 自媒体 3-媒资
    private val allBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//总binder数据
    private val hotCommentBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//热门评论列表
    private var isNewComment = false//是否是最新评论 用于最新评论和最热评论切换

    private var orientationHelper: OrientationHelper? = null
    private var isFullScreen = false//是否是全屏
    private var firstResume = false
    private var scrollHelper: DetailScrollHelper? = null
    private var isCommenting: Boolean = false//是否在发表评论中
    private var commentTotalCount = 0L//总评论数
    private var shareUrl: String = ""//分享url

    //    视频播放监听
    private var onGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_IS_CONTROLLER_SHOW)
        }

        override fun onValueUpdate(key: String?, value: Any?) {
            if (DataInter.Key.KEY_IS_CONTROLLER_SHOW == key) {
                if (isFullScreen) {
                    val show = (value as? Boolean) ?: false
                    PlayerHelper.setSystemUIVisible(this@VideoDetailActivity, show)
                }
            }
        }

    }
    private var shareAction: ((platform: SharePlatform) -> Unit)? = { platform ->
        when (platform) {
            SharePlatform.WE_CHAT,
            SharePlatform.WE_CHAT_TIMELINE,
            SharePlatform.WEI_BO,
            SharePlatform.QQ,
            SharePlatform.COPY_LINK -> {
                mViewModel?.getShareExtendInfo(CommConstant.SHARE_TYPE_MOVIE_TRAILER, videoId, extend = platform)
            }

        }

    }

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        mDataSource = it
        "加载播放地址回调-".e()
        mViewModel?.getVideoPlayUrl(videoId, videoSourceType, "http")
    }

    override fun initVM(): VideoDetailViewModel = viewModels<VideoDetailViewModel>().value

    override fun initVariable() {
        super.initVariable()
        videoId = intent?.getLongExtra(KEY_PRE_VIDEO_ID, 0L) ?: 0L
    }

    override fun initView() {
        initStateView()
        initSmartLayout()
        initBarButton()
        initVideoView()
        adapter = createMultiTypeAdapter(commentRv, LinearLayoutManager(this))
        adapter.setOnClickListener(::handleListAction)
        praiseAndCollectionObserve()
        KeyBoard.assistActivity(this, true)
        scrollHelper = DetailScrollHelper(commentRv)

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
            R.id.movieBtnFl -> {//想看，已想看，已看过
                handleWanna(binder)
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
        adapter.notifyAdapterRemoved(allBinderList.filter { it is CommentListBinder || it is CommentListEmptyBinder }.toMutableList()) {
            allBinderList.removeIf { it is CommentListBinder || it is CommentListEmptyBinder }
            hotCommentBinderList.clear()
//            "切换评论列表：$mBinderList".d()
//            mAdapter.notifyAdapterChanged(mBinderList)
            loadCommentData(false)
        }
    }

    /**
     * 处理想看和已想看状态
     */
    private fun handleWanna(binder: MultiTypeBinder<*>) {
        if (binder is VideoDetailTitleBinder) {
            when (binder.bean.getAttuideState()) {
                VIDEO_DETAIL_UN_ATTITUDE -> {//执行想看
                    mViewModel?.wantToSee(binder.bean.getMovieId(), DetailBaseViewModel.ACTION_POSITIVE, binder)
                }
                VIDEO_DETAIL_HAD_WANNA -> {//执行取消想看
                    mViewModel?.wantToSee(binder.bean.getMovieId(), DetailBaseViewModel.ACTION_CANCEL, binder)
                }
            }
        }
    }

    private fun initStateView() {
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                when (viewState) {
                    MultiStateView.VIEW_STATE_NO_NET, MultiStateView.VIEW_STATE_ERROR -> {//点击重试
                        initData()
                    }
                }
            }

        })

        var marginLayoutParams = stateView?.layoutParams as? ViewGroup.MarginLayoutParams
        marginLayoutParams?.topMargin = screenWidth * 9 / 16
        stateView?.layoutParams = marginLayoutParams
    }

    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)
    fun playVideo(vid: Long, sourceType: Long, continuePlay: Boolean) {
        mDataSource = MTimeVideoData("", vid, sourceType, 0L)
        videoDataProvider?.updateSourceData(vid, sourceType)
        val isEqualData: Boolean = PreviewVideoPlayer.get()?.isEqualData(vid) ?: false
        val isInPlaybackState: Boolean = PreviewVideoPlayer.get()?.isInPlaybackState() ?: false
        if (!isEqualData || !isInPlaybackState || !continuePlay) {
            PreviewVideoPlayer.get()?.play(mDataSource, true)
        }
    }

    private fun initVideoView() {
        orientationHelper = OrientationHelper(this)
        orientationHelper?.keepOnScreen(this)
        PlayerHelper.setSystemUIVisible(this, true)
        //--------------------------------------
        //portrait screen
        PlayerHelper.portraitMatchWidth_16_9(this, playerContainer, null)
//        orientationHelper?.updateContentContainerLocation(playerContainer)
        PreviewVideoPlayer.get()?.apply {
            setReceiverGroupConfigState(this@VideoDetailActivity, ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE)
            getReceiverGroup()?.groupValue?.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
            attachContainer(playerContainer)
            addOnReceiverEventListener(this@VideoDetailActivity)
            updateAutoPlayFlag(false)
            setDataProvider(videoDataProvider)
        }
    }

    private fun initSmartLayout() {
        smartRefreshLayout?.apply {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setOnLoadMoreListener {
                loadCommentData(true)
            }
        }
    }

    private fun initBarButton() {
        publishCommentView?.apply {
            isWithoutMovie = true
            style = PublishCommentView.Style.COMMENT
            inputEnable(true)
            action = { barType, isSelected ->
                when (barType) {
                    BarButtonItem.Type.COMMENT -> {
//                        点击评论，滚动到评论第一条  再次点击
                        scrollHelper?.handleScrollToComment(this@VideoDetailActivity, allBinderList)
                    }
                    BarButtonItem.Type.PRAISE -> {//点赞
                        handlePraiseUp(getSelectedStatusByType(barType), this@VideoDetailActivity)
                    }
                    BarButtonItem.Type.DISPRAISE -> {//点踩
                        handlePraiseDown(getSelectedStatusByType(barType), this@VideoDetailActivity)
                    }
                    BarButtonItem.Type.SEND -> {//发送评论
                        saveComment(text)
                    }
                    BarButtonItem.Type.PHOTO -> {//图片
                        showPhotoAlbumFragment(false, limitedCount = 1L).actionSelectPhotos = { photos ->
                            photos.e()
                            if (photos.isNotEmpty()) {
                                commentImgLayout.setPhotoInfo(photos[0])
                            }
                        }
                    }
                    BarButtonItem.Type.KEYBOARD -> {
                        this@VideoDetailActivity.showOrHideSoftInput(this.getEditTextView())

                    }


                }
            }
            editAction = {//点击文本如果没有登录直接跳转到登录页面
                afterLogin {
                    setEditStyle()
                }
            }

        }
    }


    override fun initData() {
        val locationId = getDigitsCurrentCityId()
        mViewModel?.loadVideoDetail(videoId, locationId)
        hotCommentBinderList.clear()
        mViewModel?.getVideoPraiseState(videoId)
        loadCommentData(false)

    }

    /**
     * 加载视频评论列表
     */
    private fun loadCommentData(isMore: Boolean) {
        mViewModel?.loadCommentList(this, videoId, TYPE_VIDEO_DETIAL, isNewComment, isMore)
    }

    /**
     * 设置视频状态
     */
    private fun setContentViewStatus(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
        publishCommentView?.visible(state == MultiStateView.VIEW_STATE_CONTENT || state == MultiStateView.VIEW_STATE_EMPTY)
    }

    /**
     * 初始化点赞信息
     */
    private fun initPraiseLayout(praiseState: PraiseState) {
        publishCommentView?.apply {
            val userPraise = praiseState.currentUserPraise ?: 0L//用户点赞状态
            isSelectedByType(BarButtonItem.Type.PRAISE, userPraise == 1L)//用户点了赞
//            isSelectedByType(BarButtonItem.Type.DISPRAISE, userPraise == 2L)//用户点了赞
            initBarButtonItem(BarButtonItem.Type.PRAISE, praiseState.upCount)
//            initBarButtonItem(BarButtonItem.Type.DISPRAISE, praiseState.downCount)

        }
    }

    private fun initBarButtonItem(type: BarButtonItem.Type, count: Long) {
        publishCommentView?.apply {
            setTipsByType(type, count)
        }
    }

    override fun startObserve() {
//        请求视频详情
        detailObserve()
//评论相关
        commentObserve()

//        视频播放地址
        playerObserve()
//底部点赞
        bottomPriseObserve()
//        分享
        shareObserve()

//        登录
        loginEventObserve()
        //        监听删除评论
        deleteCommentEventObserve()
    }

    //    评论详情中删除评论
    private fun deleteCommentEventObserve() {
        LiveEventBus.get(DELETE_COMMENT, com.kotlin.android.app.router.liveevent.event.DeleteCommentState::class.java)
                .observe(this, Observer {
                    deleteComment(it?.commentId ?: 0L)
                })
    }


    private fun deleteComment(commentId: Long) {
        CommentHelper.deleteComment(commentId, allBinderList, hotCommentBinderList, adapter)
        updateCommentTitle(true)
    }

    private fun shareObserve() {
        //        分享
        mViewModel?.shareExtendUIState?.observe(this, ShareObserver(this))

        //        想看、取消想看
        mViewModel?.wantToSeeState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (extend is VideoDetailTitleBinder) {//电影卡片
                        if ((result as? WantToSeeResult)?.isSuccess() == true) {
                            val movieBinder = extend as VideoDetailTitleBinder
                            movieBinder.setAttitude(if (movieBinder.bean.getAttuideState() == VIDEO_DETAIL_UN_ATTITUDE) VIDEO_DETAIL_HAD_WANNA else VIDEO_DETAIL_UN_ATTITUDE)
                        }
                    }
                }
            }
        })
    }

    private fun bottomPriseObserve() {
        mViewModel?.videoPraiseState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    //                    数据加载成功
                    initPraiseLayout(this)
                }
            }
        })
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

    private fun commentObserve() {
        //        热门评论列表
        mViewModel?.hotCommentListState?.observe(this, Observer {
            smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    if (hotCommentBinderList.isEmpty()) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        mergerData()
                    }
                    smartRefreshLayout?.setNoMoreData(noMoreData)
                    if (loadMore) {
                        hotCommentBinderList.addAll(this.commentBinderList)
                        allBinderList.addAll(hotCommentBinderList)
                        notifyAdapterData()
                    }
                    "最热评论中条数：${hotCommentBinderList.size}".e()
                }
            }
        })
        //最新评论
        mViewModel?.newCommentListState?.observe(this, Observer {
            smartRefreshLayout?.finishLoadMore(500)
            it?.apply {
                success?.apply {
                    hotCommentBinderList.addAll(this.commentBinderList)
                    allBinderList.addAll(hotCommentBinderList)
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

                    sendMessage(publishCommentView?.text.orEmpty(), this)
                }

                netError?.showToast()
                error?.showToast()
            }
        })

        //        删除某条评论
        mViewModel?.deleteCommentState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    dismissProgressDialog()
                    if (extend is MultiTypeBinder<*>) {
                        (extend as? MultiTypeBinder<*>)?.notifyAdapterSelfRemoved()
                        allBinderList.remove(extend)
                        hotCommentBinderList.remove(extend)
                        if (hotCommentBinderList.size == 0) {
                            val emptyBinder = CommentListEmptyBinder()
                            hotCommentBinderList.add(emptyBinder)
                            allBinderList.add(emptyBinder)
                            notifyAdapterData()
                        }

                        updateCommentTitle(true)
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
        mViewModel?.videoDetailState?.observe(this, Observer { uiModel ->
            uiModel?.apply {
                showOrHideProgressDialog(showLoading)
                if (isEmpty) {
                    setContentViewStatus(MultiStateView.VIEW_STATE_EMPTY)
                }
                success?.apply {
                    setContentViewStatus(MultiStateView.VIEW_STATE_CONTENT)
                    videoDataProvider?.refreshVideoDetail(this)
                    videoSourceType = videoSource
                    publishCommentView?.inputEnable(this.isAllowComment)
                    initBarButtonItem(BarButtonItem.Type.COMMENT, this.commentTotal)
                    playVideo(videoId, videoSource, true)
                    allBinderList.add(0, VideoDetailTitleBinder(PreVideoCommentTitleBean.covert(this)))
                    commentTotalCount = commentTotal
                    allBinderList.add(CommentListTitleBinder(CommentTitleViewBean(this.commentTotal)))
                    mergerData()
                }

                error?.apply {
                    setContentViewStatus(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    setContentViewStatus(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        })
    }

    private fun loginEventObserve() {
        LiveEventBus.get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java).observe(this, Observer {
            if (it?.isLogin == true) {//登录成功
                hotCommentBinderList.clear()
                allBinderList.clear()
                isNewComment = false
                initData()
            }
        })
    }

    private fun praiseAndCollectionObserve() {
        //        点赞、取消点赞
        mViewModel?.praiseUpState?.observe(this, CommonObserver(this, CommonObserver.TYPE_PRAISE_UP, publishCommentView, BarButtonItem.Type.PRAISE))
//        点踩、取消点踩
        mViewModel?.praiseDownState?.observe(this, CommonObserver(this, CommonObserver.TYPE_PRAISE_DOWN, publishCommentView, BarButtonItem.Type.DISPRAISE))
    }

    private fun mergerData() {
        if (isContainsCommentTitle()) {
            allBinderList.addAll(hotCommentBinderList)
        }
        notifyAdapterData()
    }

    private fun notifyAdapterData() {
        adapter.notifyAdapterDataSetChanged(allBinderList,false)
    }

    /**
     * 总列表数据中是否包含评论数据
     */
    private fun isContainsCommentTitle(): Boolean {
        return allBinderList.any { it is CommentListTitleBinder }
    }


    /**
     * 处理点赞、取消点赞操作
     */
    private fun handlePraiseUp(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        if (extend is CommentListBinder) {
            val commentId = extend.bean.commentId
            mViewModel?.praiseUpOrCancel(CommConstant.getPraiseUpType(type, CommConstant.COMMENT_PRAISE_ACTION_COMMENT), commentId, isCancel, extend)
        } else {
            mViewModel?.praiseUpOrCancel(type, videoId, isCancel, extend)
        }
    }

    /**
     * 处理点赞，取消点赞操作
     */
    private fun handlePraiseDown(isCancel: Boolean, extend: Any) {
        showInteractiveDialog()
        mViewModel?.praiseDownOrCancel(type, videoId, isCancel, extend)
    }


    /**
     * 删除评论
     */
    private fun deleteComment(binder: MultiTypeBinder<*>) {
        showInteractiveDialog()
        mViewModel?.deleteComment(type, (binder as CommentListBinder).bean.commentId, binder)
    }

    private fun saveComment(text: String) {
        if (TextUtils.isEmpty(text.trim()) && TextUtils.isEmpty(commentImgLayout?.getImagePath())) {
            showToast(com.kotlin.android.comment.component.R.string.comment_detail_cannt_send_comment)
            return
        }
        if (isCommenting) return
        mViewModel?.saveComment(type, videoId, path = commentImgLayout?.getImagePath().orEmpty(), content = text)
    }

    private fun sendMessage(text: String, commentId: Long) {
//        需要先获取commentId
        val index = allBinderList.indexOfFirst { it is CommentListBinder }
        val commentListBinder = CommentListBinder(this, CommentViewBean(commentId = commentId,
                userName = UserManager.instance.nickname,
                userId = UserManager.instance.userId,
                userPic = UserManager.instance.userAvatar.orEmpty(),
                commentContent = text,
                publishDate = formatPublishTime(System.currentTimeMillis()),
                type = type,
                objId = videoId, commentPic = mViewModel?.getUpLoadImageUrl(commentImgLayout?.getImagePath().orEmpty()).orEmpty())
        )
        commentImgLayout?.gone()
        commentImgLayout?.clearImagePath()
        val filter = allBinderList.filter { it is CommentListEmptyBinder }.toMutableList()
        adapter.notifyAdapterRemoved(filter) {
            allBinderList.removeAll(filter)
            if (index < 0) {
                allBinderList.add(commentListBinder)
            } else {
                allBinderList.add(index, commentListBinder)
            }
            CommentHelper.addCommentBinder(hotCommentBinderList, commentListBinder)

            adapter.notifyAdapterInsert(index, commentListBinder)
            allBinderList.filter { it is CommentListTitleBinder }.forEach {
                it.notifyAdapterSelfChanged()
            }

//            commentRv?.scrollToPosition(if (index < 0) 0 else index)
            publishCommentView?.resetInput()
            updateCommentTitle(false)
        }
        
    }


    /**
     * 更新标题数量
     */
    private fun updateCommentTitle(isDelete: Boolean) {
        CommentHelper.updateCommentTitleList(allBinderList, isDelete)
        if (isDelete) {
            if (commentTotalCount > 0) {
                commentTotalCount--
            }
        } else {
            commentTotalCount++
        }
        publishCommentView?.setTipsByType(BarButtonItem.Type.COMMENT, commentTotalCount)
    }


    private fun showInteractiveDialog() {
        if (isLogin()) {
            showProgressDialog()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //--------------------------------------
            //portrait screen
            PlayerHelper.portraitMatchWidth_16_9(this, playerContainer, null)
            //update content container top margin
            //update content container top margin
//            orientationHelper?.updateContentContainerLocation(playerContainer)
            //--------------------------------------
            //--------------------------------------
            isFullScreen = false
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE)
            orientationHelper?.quitFullScreen(this)
            isFullScreen = false
            PlayerHelper.setSystemUIVisible(this, true)
        } else {
            PlayerHelper.landscapeMatchWidthHeight(this, playerContainer, null)
            isFullScreen = true
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE)
        }
    }

    override fun onBackPressed() {
        if (isFullScreen) {
            orientationHelper?.toggleScreen()
        } else {
            if (publishCommentView?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
                publishCommentView?.resetInput()
                return
            }
            super.onBackPressed()
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO -> {
            }
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> {
                "横竖屏切换".e()
                orientationHelper?.toggleScreen()
            }
            DataInter.ReceiverEvent.EVENT_DANMU_COVER_SEND_DANMU -> {
            }               //全屏状态是弹幕层的弹幕发送请求,交由外层处理
            DataInter.ReceiverEvent.EVENT_CODE_SHARE -> {
//                分享
                showShareDialog(null, ShareFragment.LaunchMode.ADVANCED, SharePlatform.COPY_LINK, event = shareAction)

            }
            DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK -> {
//                showToast("反馈错误")
                val instance = getProvider(IUgcProvider::class.java)
                instance?.launchDetail(VariateExt.feedbackPostId, CommConstant.PRAISE_OBJ_TYPE_POST)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        //update receiver group config
        val configState: Int = if (isFullScreen) ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE else ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE
        PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, configState)
        //attach on back listener
        PreviewVideoPlayer.get()?.setOnBackRequestListener {
            if (isFullScreen) {
                onBackPressed()
            } else {
                finish()
            }
        }
        if (!firstResume) {
            firstResume = true
            return
        }
        PreviewVideoPlayer.get()?.attachContainer(playerContainer)
        //logic resume player
        //logic resume player
//        PreviewVideoPlayer.get()?.logicResourceResume(mDataSource)
    }

    override fun onStart() {
        super.onStart()
        orientationHelper?.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationHelper?.disable()
    }

    override fun onPause() {
        super.onPause()
        PreviewVideoPlayer.get()?.logicPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreviewVideoPlayer.get()?.removeReceiverEventListener(this)
        PreviewVideoPlayer.get()?.getReceiverGroup()?.groupValue?.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
        orientationHelper?.destroy()
        PreviewVideoPlayer.get()?.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

}