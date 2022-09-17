package com.kotlin.android.live.component.ui.widget

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kk.taurus.playerbase.receiver.ReceiverGroup
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.live.CameraPlayUrl
import com.kotlin.android.app.data.entity.live.DanmuBean
import com.kotlin.android.app.data.entity.live.DirectorUnits
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_STATUS_LIVING
import com.kotlin.android.live.component.constant.LIVE_STATUS_REVIEW
import com.kotlin.android.live.component.manager.CameraStandManager
import com.kotlin.android.live.component.receivers.DanmuCover
import com.kotlin.android.live.component.receivers.DirectorUnitsCover
import com.kotlin.android.live.component.receivers.LiveControllerCover
import com.kotlin.android.live.component.receivers.LiveErrorCover
import com.kotlin.android.player.*
import com.kotlin.android.player.bean.LiveVideoData
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.receivers.*
import com.kotlin.android.player.splayer.ISPayer
import com.kotlin.android.player.splayer.PreviewVideoPlayer

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import kotlinx.android.synthetic.main.view_live_container.view.*
import kotlinx.android.synthetic.main.view_live_player.view.*

/**
 * create by lushan on 2021/3/8
 * description:直播播放器控件
 */
class LivingPlayerView @JvmOverloads constructor(
    var ctx: Context,
    var attrs: AttributeSet? = null,
    var defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attrs, defStyleAttr), OnReceiverEventListener {
    init {
        var view = LayoutInflater.from(context).inflate(R.layout.view_live_player, null)
        addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    //    视频播放监听
    private var onGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_IS_CONTROLLER_SHOW)
        }

        override fun onValueUpdate(key: String?, value: Any?) {
            if (DataInter.Key.KEY_IS_CONTROLLER_SHOW == key) {
                if (isFullScreen) {
                    val show = (value as? Boolean) ?: false
                    getActivity()?.apply {
                        PlayerHelper.setSystemBarVisibility(this, show)
                    }
                }
            }
        }

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initVideoView()
    }

    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)


    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        mDataSource = it
        //判断是请求直播机位播放地址还是视频播放地址
        action?.invoke(it)
    }
    var action: ((MTimeVideoData) -> Unit)? = null//请求播放地址action
    var shareAction: (() -> Unit)? = null//分享触发action
    var chatAction: ((String) -> Unit)? = null//全屏情况下发送聊天
    private var isFullScreen = false//是否是全屏

    private var orientationHelper: OrientationHelper? = null
    private var firstResume = false//是否是第一次执行onResume方法


    private fun initVideoView() {
        val activity = getActivity()
        activity?.apply {
            orientationHelper = OrientationHelper(this)
            orientationHelper?.keepOnScreen(this)
            PlayerHelper.setSystemBarVisibility(this, true)
            //--------------------------------------
            //portrait screen
            PlayerHelper.portraitMatchWidth_16_9(this, playerContainer, null)
//        orientationHelper?.updateContentContainerLocation(playerContainer)
            PreviewVideoPlayer.get()?.apply {
                setReceiverGroupConfigState(
                    activity,
                    ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE,
                    getLiveCommonReceiverGroup()
                )
                getReceiverGroup()?.groupValue?.registerOnGroupValueUpdateListener(
                    onGroupValueUpdateListener
                )
                attachContainer(playerContainer)
                addOnReceiverEventListener(this@LivingPlayerView)
                updateAutoPlayFlag(false)
                setDataProvider(videoDataProvider)
            }
        }

    }

    private fun getLiveCommonReceiverGroup(): ReceiverGroup {
        return ReceiverGroup().apply {
            addReceiver(DataInter.ReceiverKey.KEY_DATA_RECEIVER, DataReceiver(context))
//        日志数据上报
//        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_LOG_RECEIVER, LogReceiver(context))
            addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, LoadingCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, LiveControllerCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, LiveErrorCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_PAUSE_AD_COVER, PauseAdCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, VideoDefinitionCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_DANMU_COVER, DanmuCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, PlayerGestureCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_DIRECTOR_UNITS_COVER, DirectorUnitsCover(context))
        }
    }

    /**
     * 播放视频
     * @param isLive 是否是直播视频
     */
    fun playVideo(vid: Long, sourceType: Long, continuePlay: Boolean, isLive: Boolean) {
        mDataSource = MTimeVideoData("", vid, sourceType, 0L)
        mDataSource.isLive = isLive
        videoDataProvider?.updateSourceData(vid, sourceType)
        val isEqualData: Boolean = PreviewVideoPlayer.get()?.isEqualData(vid) ?: false
        val isInPlaybackState: Boolean = PreviewVideoPlayer.get()?.isInPlaybackState() ?: false
        if (!isEqualData || !isInPlaybackState || !continuePlay) {
            PreviewVideoPlayer.get()?.play(mDataSource, true)
        }
    }

    /**
     * 播放视频地址请求失败
     */
    fun setVideoPlayUrlError() {
        videoDataProvider?.setVideoPlayUrlError()
    }

    fun getCurrentVideoId(): Long {
        return videoDataProvider?.getCurrentVideoId() ?: 0L
    }


    /**
     * 处理返回事件
     */
    fun backPressed(action: () -> Unit) {
        if (isFullScreen) {
            orientationHelper?.toggleScreen()
        } else {
            action.invoke()
        }
    }

    /**
     * 区分直播(机位)、直播回放、普通视频
     */
    fun setPlayerData(isCommonVideo: Boolean, liveStatus: Long) {
        initPlayerConfig(isCommonVideo, liveStatus)
    }

    fun playLiveVideo(bean: CameraPlayUrl?) {
        setPlayerData(false, liveStatus = LIVE_STATUS_LIVING)
        videoDataProvider?.setLiveVideoPlayUrlList(bean)
    }

    /**
     * 播放预告片视频，精彩回放
     */
    fun playPreVideo(videoPlayUrlBean: VideoPlayList?, isReview: Boolean = false) {
        videoPlayUrlBean ?: return
        if (isReview) {
            initPlayerConfig(false, LIVE_STATUS_REVIEW)
        }
        videoDataProvider?.setVideoPlayUrlList(videoPlayUrlBean)
        if (isReview.not()) {//精彩回放不执行该方法，避免覆盖回放标识
            videoDataProvider?.refreshVideoDetail(
                VideoDetail(
                    vId = videoDataProvider?.getCurrentVideoId()
                        ?: 0L,
                    title = CameraStandManager.getCurrentPlayVideo(videoDataProvider.getCurrentVideoId())?.title.orEmpty()
                )
            )
        } else {
            videoDataProvider?.refreshLiveVideoDetail(
                LiveVideoData(
                    LIVE_STATUS_REVIEW,
                    0L,
                    CameraStandManager.getCurrentPlayVideo(videoDataProvider.getCurrentVideoId())?.title.orEmpty()
                )
            )
        }
    }

    fun updateDanmuList(list: MutableList<DanmuBean>) {
        CameraStandManager.setDanmuList(list)
        videoDataProvider?.updateDanmuList()
    }

    fun updateDanmu(bean: DanmuBean) {
        videoDataProvider?.updateDanmu(bean)
    }

    /**
     * 更新导播台
     */
    fun updateDirectorUnits(bean: DirectorUnits){
        videoDataProvider?.updateDirectorUnits(bean)
    }

    /**
     * 直播状态下发送聊天
     */
    fun sendChat(content: String) {
        videoDataProvider?.updateChatContent(content)
    }


    /**
     * 刷新直播信息：直播状态、观看人数、直播标题
     */
    fun refreshLiveDataInfo(detail: LiveVideoData?) {
        videoDataProvider?.refreshLiveVideoDetail(detail)
    }

    /**
     * 刷新直播机位列表
     */
    fun refreshCameraStandList() {
        videoDataProvider?.refreshCameraStandList()
    }

    private fun initPlayerConfig(isCommonVideo: Boolean, liveStatus: Long) {
        PlayerConfig.isListMode = false
        PlayerConfig.liveStatus = null
        if (isCommonVideo) {
            PlayerConfig.isListMode = true
        } else {
            when (liveStatus) {
                LIVE_STATUS_LIVING -> {//直播中
                    PlayerConfig.liveStatus = LiveStatus.LIVING
                }
                LIVE_STATUS_REVIEW -> {//直播回放
                    PlayerConfig.liveStatus = LiveStatus.L_REVIEWER
                }
            }
        }
    }


    fun configChanged(view: ViewGroup?, newConfig: Configuration) {
        val activity = getActivity()
        activity?.apply {

            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                window.decorView.setBackgroundColor(getColor(R.color.color_ffffff))
                //--------------------------------------
                //portrait screen
                PlayerHelper.portraitMatchWidth_16_9(this, playerContainer, null)
                view?.let {
                    PlayerHelper.portraitMatchWidth_16_9(this, it, null)
                    val params = it.layoutParams as? MarginLayoutParams
                    params?.topMargin = statusBarHeight
                    it.layoutParams = params
                }
                //update content container top margin
                //update content container top margin
//            orientationHelper?.updateContentContainerLocation(playerContainer)
                //--------------------------------------
                //--------------------------------------
                isFullScreen = false
                PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                    this,
                    ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE
                )
                orientationHelper?.quitFullScreen(this)
//                PlayerHelper.setSystemUIVisible(this, false)
            } else {
                window.decorView.setBackgroundColor(getColor(R.color.color_000000))
                PlayerHelper.landscapeMatchWidthHeight(this, playerContainer, null)
                view?.let {
                    PlayerHelper.landscapeMatchWidthHeight(this, it, null)
                    val params = it.layoutParams as? MarginLayoutParams
                    params?.topMargin = 0
                    it.layoutParams = params
                }

                isFullScreen = true
                PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                    this,
                    ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE
                )
            }
        }
        videoDataProvider?.refreshCameraStandUIByOrientation(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)


    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO -> {//播放下一个
                val auto = bundle?.getBoolean(DataInter.Key.KEY_VIDEO_PLAY_NEXT_AUTO) ?: false
                var nextPlayVideo = CameraStandManager.getNextPlayVideo(
                    videoDataProvider?.getCurrentVideoId()
                        ?: 0L
                )
                if (nextPlayVideo == null) {//播放完成
//                  播放到最后一个视频，直接播放第一个
                    nextPlayVideo = CameraStandManager.getFistPlayVideo()
                }
                nextPlayVideo ?: return
//                播放下一个视频 相关视频默认source为3
                playVideo(nextPlayVideo.videoId, 3, false, false)
            }
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> {//横竖屏
                "横竖屏切换".e()
                orientationHelper?.toggleScreen()
                videoDataProvider?.refreshCameraStandUIByOrientation(
                    isPortrait = orientationHelper?.isPortrait
                        ?: true
                )
            }
            DataInter.ReceiverEvent.EVENT_DANMU_COVER_SEND_DANMU -> {//全屏状态是弹幕层的弹幕发送请求,交由外层处理

            }
            DataInter.ReceiverEvent.EVENT_CODE_SHARE -> {
//                分享
                shareAction?.invoke()
            }
            DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK -> {//反馈错误
//                showToast("反馈错误")
                val instance = getProvider(IUgcProvider::class.java)
                instance?.launchDetail(VariateExt.feedbackPostId, CommConstant.PRAISE_OBJ_TYPE_POST)
            }
            DataInter.ReceiverEvent.EVENT_LIVE_CAMERA_VIDEO -> {//直播切换机位
                val videoId = bundle?.getLong("videoId", 0L) ?: 0L
                playVideo(videoId, 0L, false, true)//播放视频
            }
            DataInter.ReceiverEvent.EVENT_LIVE_CHAT -> {//点击发送
                chatAction?.invoke(bundle?.getString(DataInter.Key.KEY_LIVE_CHAT_CONTENT).orEmpty())
            }

        }
    }

    fun resume() {
        //update receiver group config
        var activity = getActivity()
        activity?.apply {
            val configState: Int =
                if (isFullScreen) ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE else ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE
            PreviewVideoPlayer.get()
                ?.setReceiverGroupConfigState(this, configState, getLiveCommonReceiverGroup())
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
            try {
                PreviewVideoPlayer.get()?.attachContainer(playerContainer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            直播过程中需要继续播放，视频直接暂停
            if (PlayerConfig.liveStatus == LiveStatus.LIVING) {
//                继续播放
                PreviewVideoPlayer.get()?.requestRetry()
            }
        }
    }

    fun start() {
        orientationHelper?.enable()
    }

    fun stop() {
        orientationHelper?.disable()
    }

    fun pause() {
        PreviewVideoPlayer.get()?.logicPause()
    }

    fun release() {
        PlayerConfig.isListMode = false
        PlayerConfig.liveStatus = null
        PreviewVideoPlayer.get()?.removeReceiverEventListener(this)
        PreviewVideoPlayer.get()
            ?.getReceiverGroup()?.groupValue?.unregisterOnGroupValueUpdateListener(
            onGroupValueUpdateListener
        )
        orientationHelper?.destroy()
        PreviewVideoPlayer.get()?.destroy()
    }

}