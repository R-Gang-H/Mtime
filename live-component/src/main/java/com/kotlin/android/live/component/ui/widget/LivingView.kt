package com.kotlin.android.live.component.ui.widget

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlin.android.app.data.entity.live.CameraPlayUrl
import com.kotlin.android.app.data.entity.live.DanmuBean
import com.kotlin.android.app.data.entity.live.DirectorUnits
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_STATUS_APPOINT
import com.kotlin.android.live.component.constant.LIVE_STATUS_END
import com.kotlin.android.live.component.constant.LIVE_STATUS_LIVING
import com.kotlin.android.live.component.constant.LIVE_STATUS_REVIEW
import com.kotlin.android.live.component.manager.CameraStandManager
import com.kotlin.android.live.component.viewbean.AppointViewBean
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.LiveVideoData
import com.kotlin.android.player.bean.MTimeVideoData
import kotlinx.android.synthetic.main.view_live_container.view.*

/**
 * create by lushan on 2021/3/3
 * description:直播控件，直播前、直播后、直播中的切换、直播回顾的切换
 */
class LivingView @JvmOverloads constructor(var ctx: Context, var attrs: AttributeSet? = null, var defStyleAttr: Int = 0) : ConstraintLayout(ctx, attrs, defStyleAttr) {

    var liveStatus: Long = 0L
        //直播状态
        set(value) {
            field = value
            changeLiveLayout(value)
        }

    var playUrlAction: ((MTimeVideoData) -> Unit)? = null//请求播放地址action
    var shareLiveAction: (() -> Unit)? = null//分享触发action
    var sendChatAction: ((String) -> Unit)? = null//全屏情况下发送聊天

    init {
        LayoutInflater.from(context).inflate(R.layout.view_live_container, this)
    }

    /**
     * 切换直播布局
     */
    private fun changeLiveLayout(status: Long) {
        hideAllView()
        setLpLifeCycle(LPLifeCycle.STOP)
        when (status) {
            LIVE_STATUS_APPOINT -> {//直播预约状态
                showAppointView()
            }
            LIVE_STATUS_LIVING -> {//直播中状态
                setLpLifeCycle(LPLifeCycle.START)
                showLivingPlayerView(status)
            }
            LIVE_STATUS_END -> {//直播结束状态
                showAppointView()
            }
            LIVE_STATUS_REVIEW -> {//精彩回顾部分
                setLpLifeCycle(LPLifeCycle.START)
                showLivingPlayerView(status)
            }
        }
    }

    /**
     * 显示直播或回放
     */
    private fun showLivingPlayerView(status: Long) {
        livePlayerView?.apply {
            visible()
            shareAction = shareLiveAction
            action = playUrlAction
            chatAction = sendChatAction
            PlayerHelper.portraitMatchWidth_16_9(context, this@LivingView, null)
        }
    }

    private fun hideAllView() {
        liveAppointView?.gone()
        livePlayerView?.gone()
    }

    private fun showAppointView() {
        liveAppointView?.apply {
            visible()
            setLiveStatus(liveStatus)
//            PlayerHelper.portraitMatchWidth_16_9(context, this@LivingView, null)
        }
    }

    /**
     * 更新预约布局
     */
    fun updateAppointView(liveAppointViewBean: AppointViewBean?, backAction: ((Long) -> Unit)?, appointAction: ((Long) -> Unit)? = null, shareAction: (() -> Unit)?) {
        liveAppointView?.apply {
            setLiveStatus(liveStatus)
            setAction(appointAction, backAction, shareAction)
            updateAppointView(liveAppointViewBean)
        }
    }

    /**
     * 更新直播状态和在线观看人数
     */
    fun refreshLiveDataInfo(liveStatus: Long, onlinePersonNum: Long) {
        livePlayerView?.refreshLiveDataInfo(LiveVideoData(liveStatus, onlinePersonNum,))
    }

    /**
     * 预约成功
     */
    fun appointSuccess(appointNumber: Long) {
        showToast("预约成功")
        liveAppointView?.appointSuccess(appointNumber)
    }

    /**
     * 加载背景图片
     */
    fun showBgImageView(url: String?) {
        bgIv?.loadImage(
            data = url,
            width = 360.dp,
            height = 212.dp
        )
    }

    fun setLpLifeCycle(status: LPLifeCycle, backPressedAction: (() -> Unit)? = null) {
        when (status) {
            LPLifeCycle.RESUME -> {
                livePlayerView?.resume()
            }
            LPLifeCycle.START -> {
                if (liveStatus == LIVE_STATUS_LIVING || liveStatus == LIVE_STATUS_REVIEW) {
                    livePlayerView?.start()
                }
            }
            LPLifeCycle.STOP -> {
                livePlayerView?.stop()
            }
            LPLifeCycle.PAUSE -> {
                livePlayerView?.pause()
            }
            LPLifeCycle.RELEASE -> {
                livePlayerView?.release()
            }
            LPLifeCycle.BACKPRESSED -> {
                backPressedAction?.apply {
                    livePlayerView?.backPressed(backPressedAction)
                }
            }
        }
    }

    fun setConfig(view: ViewGroup?, newConfig: Configuration) {
//        if (liveStatus == LIVE_STATUS_LIVING || liveStatus == LIVE_STATUS_REVIEW) {//直播中和直播回放处理转屏问题
            livePlayerView?.configChanged(view, newConfig)
//        }
    }

    /**
     * 播放机位视频
     * 1.预约状态下播放相关视频切换到直播，需要直接播放直播
     * 2.直播状态下播放相关视频，切换更新，还是播放相关视频
     * 3.播放相关视频时 PlayerConfig.liveStatus置成空了
     * 上次直播状态
     *@param isUpdate 是否是切换直播状态后请求机位列表
     */
    fun playCameraVideo(videoId: Long,isUpdate:Boolean, continuePlay: Boolean = false) {
        val currentVideoId = livePlayerView?.getCurrentVideoId() ?: 0L
        //预约的相关视频播放切换为直播，需要直接切换为直播；   直播中播放相关视频，切换了机位不播放直播
        if (isUpdate.not() && currentVideoId != 0L && PlayerConfig.liveStatus == null) return //如果此时播出的是相关视频，不用走下面逻辑
//        如果当前没有播放视频，或者当前播放的视频不在机位列表中，需要播放第一个视频
        if (currentVideoId == 0L || CameraStandManager.containsVideoId(currentVideoId).not()) {
            livePlayerView?.playVideo(videoId, 0L, continuePlay, true)
            CameraStandManager.updateCameraVideoId(videoId)
        } else {
            CameraStandManager.updateCameraVideoId(currentVideoId)
        }
        refreshCameraStandList()
    }

    fun refreshCameraStandList(){
        livePlayerView?.refreshCameraStandList()
    }

    /**
     * 机位视频地址变更
     * 如果是同一个视频，需要重新播放，如果不是同一个视频，不用处理
     */
    fun cameraStreamUrlChange(videoId: Long) {
        val currentVideoId = livePlayerView?.getCurrentVideoId()
        if (currentVideoId == videoId) {
            livePlayerView?.playVideo(videoId, 0L, false, true)
        }
    }


    /**
     * 播放机位视频
     */
    fun playLiveVideo(bean: CameraPlayUrl) {
        livePlayerView?.playLiveVideo(bean)
    }

    fun playVideo(videoId: Long,liveStatus: Long,isReview: Boolean = false){
        livePlayerView?.playPreVideo(CameraStandManager.getPlayVideoPlayListByVideoId(videoId),isReview)
    }


    /**
     * 全屏情况下发送请求，如果为空字符串，表示发送成功，需要把播放器中输入框清除
     */
    fun sendChat(content: String) {
        livePlayerView?.sendChat(content)
    }

    /**
     * 播放点击的视频
     */
    fun playReleateVideo(videoId: Long, isReviewVideo: Boolean = false) {
        changeLiveLayout(if (isReviewVideo) LIVE_STATUS_REVIEW else LIVE_STATUS_LIVING)//切换到视频播放
//        source默认是3
        livePlayerView?.playVideo(videoId, 3, false, false)
    }

    fun setPlayerData(isCommonVideo: Boolean, liveStatus: Long) {
        livePlayerView?.setPlayerData(isCommonVideo,liveStatus)
    }

    /**
     * 播放器地址加载失败
     */
    fun setVideoPlayUrlError() {
        livePlayerView?.setVideoPlayUrlError()
    }

    fun updateDanmuList(list: MutableList<DanmuBean>) {
        livePlayerView?.updateDanmuList(list)
    }

    fun updateDanmu(bean: DanmuBean) {
        livePlayerView?.updateDanmu(bean)
    }

    fun updateDirectorUnits(bean:DirectorUnits){
        livePlayerView?.updateDirectorUnits(bean)
    }

    public enum class LPLifeCycle {
        RESUME,
        START,
        STOP,
        PAUSE,
        RELEASE,
        BACKPRESSED
    }


}