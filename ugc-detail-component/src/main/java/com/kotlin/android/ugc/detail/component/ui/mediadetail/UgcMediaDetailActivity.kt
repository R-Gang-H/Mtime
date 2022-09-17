package com.kotlin.android.ugc.detail.component.ui.mediadetail

import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_AUDIO
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_OBJ_TYPE_AUDIO
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_OBJ_TYPE_VIDEO
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_AUDIO
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_VIDEO
import com.kotlin.android.app.data.constant.CommConstant.SHARE_TYPE_AUDIO
import com.kotlin.android.app.data.constant.CommConstant.SHARE_TYPE_VIDEO
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingMagnetView
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingView
import com.kotlin.android.audio.floatview.component.aduiofloat.MagnetViewListener
import com.kotlin.android.audio.player.constant.AudioActionManager
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bean.CommentTitleViewBean
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.helper.CommentHelper
import com.kotlin.android.comment.component.widget.CommentImageLayout
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.KeyBoard
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.lifecycle.KtxActivityManager
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.ext.topMargin
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.ReceiverGroupManager
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.splayer.ISPayer
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import com.kotlin.android.player.widgets.videodialog.VideoDialogFragment
import com.kotlin.android.player.widgets.videodialog.dismissVideoDialog
import com.kotlin.android.player.widgets.videodialog.showVideoDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcMediaViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcMediaViewBean.Companion.VIDEO_TYPE_LAND
import com.kotlin.android.ugc.detail.component.bean.UgcMediaViewBean.Companion.VIDEO_TYPE_PORTRAIT
import com.kotlin.android.ugc.detail.component.binder.UgcLinkMovieBinder
import com.kotlin.android.ugc.detail.component.binder.UgcMediaInfoBinder
import com.kotlin.android.ugc.detail.component.contentCanShare
import com.kotlin.android.ugc.detail.component.databinding.ActivityUgcMediaDetailBinding
import com.kotlin.android.ugc.detail.component.observe.WantToSeeObserve
import com.kotlin.android.ugc.detail.component.ui.BaseUgcDetailActivity
import com.kotlin.android.ugc.detail.component.ui.link.dismissUgcLinkDialog
import com.kotlin.android.ugc.detail.component.ui.link.showUgcLinkDialog
import com.kotlin.android.ugc.detail.component.ui.widget.UgcTitleView
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlin.math.abs

/**
 * Ugc视频详情、音乐详情
 */
@Route(path = RouterActivityPath.UgcDetail.PAGE_UGC_MEDIA_DETAIL_ACTIVITY)
class UgcMediaDetailActivity :
    BaseUgcDetailActivity<UgcMediaViewModel, ActivityUgcMediaDetailBinding>(),
    OnReceiverEventListener, DialogInterface.OnDismissListener {
    private var orientationHelper: OrientationHelper? = null
    private var isFullScreen = false//是否是全屏
    private var videoShowType: Long = VIDEO_TYPE_LAND
    private var videoBean: VideoPlayList? = null
    private val landHeight = 240.dp
    private val porHeight = 576.dp
    private val audioHeight = 384.dp
    private val linkBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()

    //    视频播放监听
    private var onGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_IS_CONTROLLER_SHOW)
        }

        override fun onValueUpdate(key: String?, value: Any?) {
            if (DataInter.Key.KEY_IS_CONTROLLER_SHOW == key) {
                if (isFullScreen) {
                    val show = (value as? Boolean) ?: false
                    PlayerHelper.setSystemUIVisible(this@UgcMediaDetailActivity, show)
                }
            }
        }

    }


    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initView() {
        super.initView()
        if (isAudio()) {
            AudioActionManager.startAudioService()
        }
        mBinding?.albumTitleView?.apply {
            setTitleColor(isDark = true)
            topStatusMargin()
            showOnlyBackAndMore()
            setListener(moreClick = {
                contentStatus.contentCanShare(if (passType == CONTENT_TYPE_VIDEO) R.string.ugc_detail_component_video_is_checking else R.string.ugc_detail_component_audio_is_checking) {
                    showShareDialog()
                }

            },
                back = {
                    onBackPressed()
                })
        }

        mBinding?.appBarLayout?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (isVideo()) {
                if (videoShowType == UgcMediaViewBean.VIDEO_TYPE_PORTRAIT) {//竖版需要改变当前视频大小
                    setVideoHeight(porHeight - abs(verticalOffset))
                    mBinding?.videoFl?.topMargin(abs(verticalOffset))
                }
            }
            if (isAudio()) {//改变音频标题栏颜色
                val isDark =
                    abs(verticalOffset) <= appBarLayout.totalScrollRange - statusBarHeight - 44.dp

                val alpha = if (!isDark) {
                    255
                } else {
                    (255 * (abs(verticalOffset)).toFloat() / (appBarLayout.totalScrollRange - statusBarHeight - 44.dp)).toInt()
                }
                val colorInt = Color.argb(alpha, 255, 255, 255)
                mBinding?.albumTitleView?.setTitleBackgroundColor(colorInt)
                mBinding?.albumTitleView?.setTitleColor(isDark)
                immersive().updateStatusBarColor(colorInt)
                    .statusBarDarkFont(!isDark)
            }
        })


        mBinding?.linkView?.onClick {
            showUgcLinkDialog(linkBinderList) {
                if (it is UgcLinkMovieBinder) {
                    //想看、取消想看
                    if (it.bean.isWanna()) {//显示想看，执行想看操作
                        mViewModel?.wantToSee(
                            it.bean.movieId,
                            DetailBaseViewModel.ACTION_POSITIVE, it
                        )
                    } else {//显示已想看，执行取消想看操作
                        mViewModel?.wantToSee(
                            it.bean.movieId,
                            DetailBaseViewModel.ACTION_CANCEL, it
                        )
                    }
                }
            }
        }
        if (isVideo()) {
            initVideoView()
        }


    }

    private fun showAudioFloatView() {
        //需要发送完整信息
        FloatingView.get()?.add(CoreApp.instance)
        FloatingView.get()?.listener(object : MagnetViewListener {
            override fun onRemove(magnetView: FloatingMagnetView?) {

            }

            override fun onClick(magnetView: FloatingMagnetView?) {
                if (KtxActivityManager.topActivity?.localClassName == this@UgcMediaDetailActivity.localClassName && isAudio()) {//如果当前activity是音频详情
                    return
                }
                getProvider(IUgcProvider::class.java) {
                    startUgcMediaDetail(contentId, passType, recId)
                }
            }
        })
        mBinding?.audioView?.updateInfo()
    }


    private fun initVideoView() {
        orientationHelper = OrientationHelper(this)
        orientationHelper?.keepOnScreen(this)
        orientationHelper?.sensorEnable(false)
        PlayerHelper.setSystemUIVisible(this, true)
        mBinding?.videoFl?.apply {
            PlayerHelper.portraitMatchWidth_16_9(this@UgcMediaDetailActivity, this, null)
        }
        PreviewVideoPlayer.get()?.apply {
            getReceiverGroup()?.groupValue?.registerOnGroupValueUpdateListener(
                onGroupValueUpdateListener
            )
            attachContainer(mBinding?.videoFl)
            addOnReceiverEventListener(this@UgcMediaDetailActivity)
            updateAutoPlayFlag(false)
            setDataProvider(videoDataProvider)
            setReceiverGroupConfigState(
                this@UgcMediaDetailActivity,
                ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE,
                getNoGestureRgroup()
            )
            setOnBackRequestListener {
                onBackPressed()
            }
            sizeListener = { width, height ->
                videoShowType = if (height > width) {
                    VIDEO_TYPE_PORTRAIT
                } else {
                    VIDEO_TYPE_LAND
                }
                updateVideoSize()
            }
        }
    }

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        setVideoData()
    }

    private fun setVideoData() {
        videoBean?.apply {
            videoDataProvider?.setVideoPlayUrlList(this)
        }
    }


    override fun initData() {
        //请求
        mViewModel?.loadDetail(passType, contentId, recId)
        loadCommentData(false)
    }


    override fun startObserve() {
        super.startObserve()
        detailObserve()
        attentionObserve()
        wannaObserve()
    }

    private fun wannaObserve() {
        mViewModel?.wantToSeeState?.observe(this, object : WantToSeeObserve(this) {
            //想看电影，取消想看电影
            override fun onChanged(t: BaseUIModel<DetailBaseExtend<Any>>?) {
                super.onChanged(t)
                t?.apply {
                    success?.apply {
                        if (extend is UgcLinkMovieBinder) {//关联电影卡片
                            if ((result as? WantToSeeResult)?.isSuccess() == true) {
                                val movieBinder = extend as UgcLinkMovieBinder
                                movieBinder.updateWannaStatus()
                                linkBinderList.filter { it is UgcLinkMovieBinder && it.bean.movieId == movieBinder.bean.movieId }
                                    .forEach {
                                        (it as UgcLinkMovieBinder).bean.movieStatus =
                                            movieBinder.bean.movieStatus
                                    }
                            }
                        }
                    }
                    error?.showToast()
                    netError?.showToast()

                }
            }
        })
    }

    private fun attentionObserve() {
        mViewModel?.followExtState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    val b = result as Boolean
                    val ugcMediaInfoBinder = extend as UgcMediaInfoBinder
                    if (ugcMediaInfoBinder.bean.isFollow) {//已经是关注了，此时是取消关注
                        if (b) {
                            showToast(R.string.ugc_detail_component_cancel_attention_success)
                        } else {
                            showToast(R.string.ugc_detail_component_cancel_attention_failed)
                        }
                    } else {
                        if (b) {
                            showToast(R.string.ugc_follow_success)
                        } else {
                            showToast(R.string.ugc_detail_component_attention_failed)
                        }
                    }
                    if (b) {
                        ugcMediaInfoBinder.updateFollow()
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        })
    }


    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)
    fun playVideo(bean: VideoPlayList) {
        mDataSource = MTimeVideoData("", 0, 0, 0L)
        videoDataProvider?.updateSourceData(0, 0)
        val isEqualData: Boolean = PreviewVideoPlayer.get()?.isEqualData(0) ?: false
        val isInPlaybackState: Boolean = PreviewVideoPlayer.get()?.isInPlaybackState() ?: false
        if (!isEqualData || !isInPlaybackState) {
            PreviewVideoPlayer.get()?.play(mDataSource, true)
        }
        videoDataProvider?.setVideoPlayUrlList(bean)
    }

    override fun isVideo() = passType == CONTENT_TYPE_VIDEO

    private fun isLandVideo() = videoShowType == VIDEO_TYPE_LAND

    override fun isAudio() = passType == CONTENT_TYPE_AUDIO

    private fun detailObserve() {
        mViewModel?.detailUIState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    setContentState(mBinding?.stateView, MultiStateView.VIEW_STATE_CONTENT)
                    commonBar?.apply {
                        contentStatus = this.createUser.contentStatus
                        this@UgcMediaDetailActivity.editBtn = this.editBtn
                        initCommonLayout(this)
                    }
                    authUserId = mediaInfoBean?.userId.orZero()
                    //播放视频 视频情况下滑动到横屏视频处，音频直接滑动到顶部
                    mBinding?.collapTL?.minimumHeight =
                        if (isVideo()) landHeight else 0


//                    mBinding?.albumTitleView?.visible(isVideo().not())//音频播放器使用title
                    mBinding?.videoFl?.minimumHeight = if (isVideo()) landHeight else audioHeight
                    if (isVideo()) {
                        mBinding?.videoFl?.visible()
                        this@UgcMediaDetailActivity.videoBean = videoBean
                        videoBean?.apply {
                            playVideo(this)
                        }
                    }


                    if (isAudio()) {
                        mBinding?.audioView?.apply {
                            layoutParams?.apply {
                                height = audioHeight
                            }
                            visible()
                            audioViewBean?.apply {
                                setData(this)
                            }
                        }

                    }


                    //展示文案专区
                    KeyBoard.assistActivity(this@UgcMediaDetailActivity, true)
                    mediaInfoBean?.apply {
                        detailBinderList.clear()
                        detailBinderList.add(UgcMediaInfoBinder(this))
                        detailBinderList.add(
                            CommentListTitleBinder(
                                CommentTitleViewBean(commonBar?.commentSupport?.commentCount.orZero())
                            )
                        )
                    }

                    mergerBinder()
                    mBinding?.linkView?.visible(ugcLinkBinderList.isNotEmpty())
                    linkBinderList.clear()
                    linkBinderList.addAll(ugcLinkBinderList)
                    mBinding?.albumTitleView?.setTitleColor(true)
                    mViewModel?.submitContentTrace(passType, contentId.toString(), authUserId)
                }
                netError?.run {
                    mBinding?.albumTitleView?.setTitleColor(false)
                    setContentState(mBinding?.stateView, MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.run {
                    mBinding?.albumTitleView?.setTitleColor(false)
                    setContentState(mBinding?.stateView, MultiStateView.VIEW_STATE_ERROR)
                }
                if (isEmpty) {
                    mBinding?.albumTitleView?.setTitleColor(false)
                    setContentState(mBinding?.stateView, MultiStateView.VIEW_STATE_EMPTY)
                }

            }
        })
    }

    private fun updateVideoSize() {
        mBinding?.videoFl?.layoutParams?.apply {
            height = if (isVideo()) {
                if (isLandVideo().not()) porHeight else if (isFullScreen) screenHeight else landHeight
            } else {
                audioHeight
            }
        }
    }

    override fun mergerBinder() {
        synchronized(this) {
            var isNotContainsMediaInfoBinder = isContainsMediaInfoBinder().not()
            if (isNotContainsMediaInfoBinder) {
                mBinderList.addAll(0, detailBinderList)
            }
            mBinderList.removeAll { it is CommentListBinder || it is CommentListEmptyBinder }

            mBinderList.addAll(hotCommentBinderList)
            "加载成功后mBinderList:$mBinderList".d()
            "hotCommentBinderList:$hotCommentBinderList".d()
            notifyAdapterData()
        }
    }

    private fun isContainsMediaInfoBinder(): Boolean {
        synchronized(this) {
            val titleIndex = mBinderList.indexOfFirst { it is UgcMediaInfoBinder }
            return titleIndex >= 0
        }
    }

    /**
     * 初始化标题和评论
     */
    private fun initCommonLayout(ugcCommonBarBean: UgcCommonBarBean) {
        this.commonBarBean = ugcCommonBarBean
        resetInput(CommentHelper.UpdateBarState.INIT)
    }

    override fun updateFollow(isFollowed: Boolean) {

    }

    override fun handleListAction(view: View, binder: MultiTypeBinder<*>) {
        super.handleListAction(view, binder)
        when (view.id) {
            R.id.attentionFL -> {//关注
                if (binder is UgcMediaInfoBinder) {
                    if (binder.bean.isFollow) {
                        showCancelAttentionDialog(binder.bean.userId, binder)
                    } else {
                        mViewModel?.followExt(binder.bean.userId, true, binder)
                    }
                }
            }
        }
    }

    override fun getSmartLayout(): SmartRefreshLayout? {
        return mBinding?.smartRefreshLayout
    }

    override fun getBarButton(): PublishCommentView? {
        return mBinding?.barButton
    }

    override fun getImageLayout(): CommentImageLayout? {
        return mBinding?.commentImgLayout
    }

    override fun getMultiStateView(): MultiStateView? {
        return mBinding?.stateView
    }

    override fun getRecyclerView(): RecyclerView? {
        return mBinding?.articleRv
    }

    override fun getCollectionType(): Long {
        return if (isVideo()) COLLECTION_OBJ_TYPE_VIDEO else COLLECTION_OBJ_TYPE_AUDIO
    }

    override fun getTitleView(): UgcTitleView? {
        return mBinding?.albumTitleView
    }

    override fun onResume() {
        if (isAudio()) {
            FloatingView.get()?.remove()
            //需要判断当前播放的和本页面播放的是不是同一个音频
            mBinding?.audioView?.checkAudio()
        }
        if (isVideo()) {
            val configState: Int =
                if (isFullScreen) ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE else ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, configState)
            PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoFl)
            PreviewVideoPlayer.get()?.logicResourceResume(mDataSource)
        }
        super.onResume()
    }

    private fun getNoGestureRgroup() =
        ReceiverGroupManager.getStandardWithoutGestureReceiverGroup(this)

    private fun getStandardRgroup() = ReceiverGroupManager.getStandardReceiverGroup(this)

    override fun onStart() {
        if (isVideo()) {
            orientationHelper?.enable()
        }
        super.onStart()
    }

    override fun onStop() {
        if (isVideo()) {
            orientationHelper?.disable()
        }
        super.onStop()
    }

    override fun onPause() {
        if (isAudio() && mBinding?.stateView?.getViewState() == MultiStateView.VIEW_STATE_CONTENT) {
            showAudioFloatView()
        }
        if (isVideo()) {
            PreviewVideoPlayer.get()?.logicPause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (isVideo()) {
            PreviewVideoPlayer.get()?.removeReceiverEventListener(this)
            PreviewVideoPlayer.get()
                ?.getReceiverGroup()?.groupValue?.unregisterOnGroupValueUpdateListener(
                    onGroupValueUpdateListener
                )
            orientationHelper?.destroy()
            PreviewVideoPlayer.get()?.destroy()
        }
        super.onDestroy()
    }

    private var videoFragment: VideoDialogFragment? = null
    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> {
                "横竖屏切换".e()
                if (isLandVideo()) {//横屏视频
                    orientationHelper?.toggleScreen()
                } else {//竖版视频 点击全屏，显示全屏弹框显示播放器，且不显示全屏按钮
                    if (videoFragment?.isVisible == true) {
                        dismissVideoDialog()
                        PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                            this@UgcMediaDetailActivity,
                            ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE,
                            getNoGestureRgroup()
                        )
                        PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoFl)
                        return
                    }
                    PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                        this@UgcMediaDetailActivity,
                        ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE, getStandardRgroup()
                    )
                    videoFragment = showVideoDialog().apply {


                        mBinding?.videoFl?.removeAllViews()
                    }


                }
            }
            DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK -> {
//                showToast("反馈错误")
                getProvider(IUgcProvider::class.java) {
                    launchDetail(VariateExt.feedbackPostId, CommConstant.PRAISE_OBJ_TYPE_POST)
                }
            }
            DataInter.ReceiverEvent.EVENT_CODE_SHARE -> {
                showShareDialog()
            }

        }
    }

    override fun onBackPressed() {
        if (videoFragment?.isVisible == true) {
            dismissVideoDialog()
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                this,
                ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
            )
            PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoFl)
        } else if (isFullScreen) {
            orientationHelper?.toggleScreen()
        } else {
            if (mBinding?.barButton?.style == PublishCommentView.Style.EDIT_WITHOUT_MOVIE) {
                mBinding?.barButton?.resetInput()
                return
            }
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setVideoHeight(if (isLandVideo()) landHeight else porHeight)
            isFullScreen = false
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                this,
                ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
            )
            orientationHelper?.quitFullScreen(this)
            PlayerHelper.setSystemUIVisible(this, true)
            mBinding?.albumTitleView?.visible(true)
            mBinding?.collapTL?.minimumHeight = if (isVideo()) landHeight else 0
        } else {
            mBinding?.albumTitleView?.visible(false)
            mBinding?.videoFl?.apply {
                PlayerHelper.landscapeMatchWidthHeight(this@UgcMediaDetailActivity, this, null)
            }

            isFullScreen = true
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                this,
                ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE
            )
            mBinding?.collapTL?.minimumHeight = screenHeight
        }
    }

    private fun setVideoHeight(height: Int) {
        mBinding?.videoFl?.apply {
            PlayerHelper.portraitMatchWidthWithHeight(
                this@UgcMediaDetailActivity,
                this,
                null,
                height
            )
        }
    }

    private fun showCancelAttentionDialog(userId: Long, binder: MultiTypeBinder<*>) {
        BaseDialog.Builder(this).setContent(R.string.ugc_detail_component_confirm_cancel_attention)
            .setPositiveButton(R.string.ok) { dialog, id ->
                mViewModel?.followExt(userId, false, binder)
                dialog?.dismiss()
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dismissShareDialog()
                dialog?.dismiss()
            }.create().show()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
            this,
            ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
        )
        PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoFl)
    }

    override fun getShareType(): Long {
        return if (isVideo()) SHARE_TYPE_VIDEO else SHARE_TYPE_AUDIO
    }

    override fun getPraiseType(): Long {
        return if (isVideo()) PRAISE_OBJ_TYPE_VIDEO else PRAISE_OBJ_TYPE_AUDIO
    }

    override fun gotoEdit() {
        super.gotoEdit()
        getProvider(IPublishProvider::class.java) {
            startVideoPublishActivity(contentId, editBtn?.recId.orZero())
        }
    }

}