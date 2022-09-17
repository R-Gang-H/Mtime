package com.kotlin.android.publish.component.ui.video

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.app.router.liveevent.event.CloseState
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.splayer.ISPayer
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import com.kotlin.android.player.widgets.videodialog.VideoDialogFragment
import com.kotlin.android.player.widgets.videodialog.dismissVideoDialog
import com.kotlin.android.player.widgets.videodialog.showVideoDialog
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ActPreviewVideoBinding
import com.kotlin.android.publish.component.widget.selector.LocalMedia
import com.kotlin.android.router.bus.ext.observe
import com.kotlin.android.router.ext.getProvider

/**
 * create by lushan on 2022/4/7
 * des:选择本地视频预览
 **/
@Route(path = RouterActivityPath.Publish.PAGER_PREVIEW_VIDEO_ACTIVITY)
class PreviewVideoActivity : BaseVMActivity<PreviewVideoViewModel, ActPreviewVideoBinding>(),
    OnReceiverEventListener , DialogInterface.OnDismissListener{
    companion object {
        const val KEY_VIDEO_BEAN = "key_video_bean"
        const val KEY_TO_VIDEO_PUBLISH = "key_to_video_publish"
    }

    private var orientationHelper: OrientationHelper? = null
    private var isFullScreen = false//是否是全屏
    private var videoBean: VideoPlayList? = null
    private var localMedia: LocalMedia? = null
    private var isToVideoPublish: Boolean = true
    private var videoFragment: VideoDialogFragment? = null
    //    视频播放监听
    private var onGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_IS_CONTROLLER_SHOW, DataInter.Key.KEY_LIST_COMPLETE)
        }

        override fun onValueUpdate(key: String?, value: Any?) {
            when (key) {
                DataInter.Key.KEY_IS_CONTROLLER_SHOW -> {
                    if (isFullScreen) {
                        val show = (value as? Boolean) ?: false
                        PlayerHelper.setSystemUIVisible(this@PreviewVideoActivity, show)
                    }
                }
                DataInter.Key.KEY_LIST_COMPLETE -> {
                    PreviewVideoPlayer.get()?.requestRetry()
                }

            }
        }

    }

    /**
     * 视频播放数据提供类
     */
    private val videoDataProvider: MTimeDataProvider? = MTimeDataProvider {
        setVideoData()
    }

    override fun initVariable() {
        super.initVariable()
        localMedia = intent?.getParcelableExtra<LocalMedia>(KEY_VIDEO_BEAN)
        isToVideoPublish = intent?.getBooleanExtra(KEY_TO_VIDEO_PUBLISH, true).orFalse()
    }

    private fun setVideoData() {
        videoBean?.apply {
            videoDataProvider?.setVideoPlayUrlList(this)
        }
    }

    override fun initView() {
        initVideoView()
    }

    private fun initVideoView() {
        orientationHelper = OrientationHelper(this)
        orientationHelper?.keepOnScreen(this)
        orientationHelper?.sensorEnable(false)
        PlayerHelper.setSystemUIVisible(this, true)
        PreviewVideoPlayer.get()?.apply {
            setReceiverGroupConfigState(
                this@PreviewVideoActivity,
                ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
            )
            getReceiverGroup()?.groupValue?.registerOnGroupValueUpdateListener(
                onGroupValueUpdateListener
            )
            attachContainer(mBinding?.videoContainer)
            addOnReceiverEventListener(this@PreviewVideoActivity)
            updateAutoPlayFlag(true)
            setFullScreenVisibility(false)
            setDataProvider(videoDataProvider)
            setOnBackRequestListener {
                if (videoFragment?.isVisible == true){
                    if (PreviewVideoPlayer.get()?.isLandScape(this@PreviewVideoActivity) == true){
                        orientationHelper?.toggleScreen()
                        return@setOnBackRequestListener
                    }
                    dismissVideoDialog()
                    PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                        this@PreviewVideoActivity,
                        ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
                    )
                    PreviewVideoPlayer.get()?.setShareVisibility(false)
                    PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoContainer)
                }else {
                    onBackPressed()
                }
            }
        }
    }

    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)
    fun playVideo() {
        val bean: VideoPlayList =
            VideoPlayList(arrayListOf(VideoPlayUrl(url = localMedia?.realPath.orEmpty())))
        videoBean = bean
        mDataSource = MTimeVideoData("", 0, 0, 0L)
        videoDataProvider?.updateSourceData(0, 0)
        val isEqualData: Boolean = PreviewVideoPlayer.get()?.isEqualData(0) ?: false
        val isInPlaybackState: Boolean = PreviewVideoPlayer.get()?.isInPlaybackState() ?: false
        if (!isEqualData || !isInPlaybackState) {
            PreviewVideoPlayer.get()?.play(mDataSource, true)
        }
        videoDataProvider?.setVideoPlayUrlList(bean, false)
    }

    override fun initData() {
        playVideo()
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mBinding?.titleBar?.addItem(
            drawableRes = R.drawable.ic_title_bar_back_dark,
            reverseDrawableRes = R.drawable.ic_title_bar_back_light
        ) {
            onBackPressed()
        }?.addItem(//发布
            titleRes = R.string.publish_component_next_step,
            colorRes = R.color.color_20a0da,
            textSize = 14F,
            isBold = true,
            isReversed = true,
            titlePaddingStart = 10.dp,
            titlePaddingEnd = 10.dp
        ) {
            if (isToVideoPublish) {
                getProvider(IPublishProvider::class.java) {
                    startVideoPublishActivity(0L, 0L, localMedia?.realPath.orEmpty())
                }
            } else {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(KEY_VIDEO_BEAN, localMedia)
                })
            }
            finish()

        }
        mBinding?.titleBar?.topStatusMargin()
    }

    override fun startObserve() {
        observe(CloseState::class.java, Observer {
            finish()
        })
    }

    override fun onBackPressed() {
        if (videoFragment?.isVisible == true){
            if (PreviewVideoPlayer.get()?.isLandScape(this) == true){
                orientationHelper?.toggleScreen()
                return
            }

        }
        super.onBackPressed()
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        LogUtils.e("播放本地视频", "播放本地视频：$eventCode -- bundle:${bundle.toString()}")
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_VIDEO_COMPLETE -> {
                playVideo()
            }
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE->{//点击了全屏
                if (videoFragment?.isVisible == true) {
                    orientationHelper?.toggleScreen()
//                    dismissVideoDialog()
//                    PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
//                        this@PreviewVideoActivity,
//                        ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
//                    )
//                    PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoContainer)
                    return
                }
                videoFragment = showVideoDialog().apply {

                    PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
                        this@PreviewVideoActivity,
                        ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE
                    )
                    PreviewVideoPlayer.get()?.setShareVisibility(false)
                    mBinding?.videoContainer?.removeAllViews()
//                    dialog?.setOnDismissListener {
//                        handleDismissDialog()
//                    }
                }
            }

        }
    }
    private fun handleDismissDialog(){
        if (PreviewVideoPlayer.get()?.isLandScape(this) == true){
            orientationHelper?.toggleScreen()
        }
        PreviewVideoPlayer.get()?.setReceiverGroupConfigState(
            this@PreviewVideoActivity,
            ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
        )
        PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoContainer)
    }

    override fun onResume() {
        val configState: Int =
            if (isFullScreen) ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE else ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE
        PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, configState)
        PreviewVideoPlayer.get()?.attachContainer(mBinding?.videoContainer)
        PreviewVideoPlayer.get()?.logicResourceResume(mDataSource)
        super.onResume()
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
        PreviewVideoPlayer.get()?.logicPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreviewVideoPlayer.get()?.removeReceiverEventListener(this)
        PreviewVideoPlayer.get()
            ?.getReceiverGroup()?.groupValue?.unregisterOnGroupValueUpdateListener(
                onGroupValueUpdateListener
            )
        orientationHelper?.destroy()
        PreviewVideoPlayer.get()?.destroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        handleDismissDialog()
    }
}