package debug.main

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.permission.permissions
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.OrientationHelper
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.splayer.ISPayer
import com.kotlin.android.player.splayer.PreviewVideoPlayer

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.video.IVideoProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.video.component.R
import kotlinx.android.synthetic.main.activity_video_debug.*

class VideoDebugActivity : AppCompatActivity(), OnReceiverEventListener {
    private var orientationHelper: OrientationHelper? = null
    private var isFullScreen = false
    private val videoProvider:IVideoProvider? = getProvider(IVideoProvider::class.java)
    private var onGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_IS_CONTROLLER_SHOW)
        }

        override fun onValueUpdate(key: String?, value: Any?) {
            if (DataInter.Key.KEY_IS_CONTROLLER_SHOW == key) {
                if (isFullScreen) {
                    val show = (value as? Boolean) ?: false
                    PlayerHelper.setSystemUIVisible(this@VideoDebugActivity, show)
                }
            }
        }

    }

    private var firstResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_debug)

        playVideo(1L, 1L, false)
        initVideoView()
        videoDetailBtn?.onClick {
            videoProvider?.startPreVideoActivity(123L)
        }

        permissions(Manifest.permission.READ_EXTERNAL_STORAGE){
            onShowRationale {
                request(it)
            }

        }
    }

    private fun initVideoView() {
        keepOnScreen()
        orientationHelper = OrientationHelper(this)
        PlayerHelper.setSystemUIVisible(this, true)
        //--------------------------------------
        //portrait screen
        PlayerHelper.portraitMatchWidth_16_9(this, videoContainer, null)
        updateContentContainerLocation()

        PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE)
        PreviewVideoPlayer.get()?.getReceiverGroup()?.groupValue?.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
        PreviewVideoPlayer.get()?.attachContainer(videoContainer)

        PreviewVideoPlayer.get()?.addOnReceiverEventListener(this)
        PreviewVideoPlayer.get()?.updateAutoPlayFlag(false)

    }

    private fun updateContentContainerLocation() {
        val layoutParams: ViewGroup.LayoutParams = videoContainer.layoutParams
        val contentLayoutParams: ViewGroup.LayoutParams = videoContainer.layoutParams
        if (contentLayoutParams is RelativeLayout.LayoutParams) {
            contentLayoutParams.topMargin = layoutParams.height
        } else if (contentLayoutParams is LinearLayout.LayoutParams) {
            contentLayoutParams.topMargin = layoutParams.height
        }
        videoContainer.layoutParams = contentLayoutParams
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //--------------------------------------
            //portrait screen
            PlayerHelper.portraitMatchWidth_16_9(this, videoContainer, null)
            //update content container top margin
            //update content container top margin
            updateContentContainerLocation()
            //--------------------------------------
            //--------------------------------------
            isFullScreen = false
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE)
            quitFullScreen()
            PlayerHelper.setSystemUIVisible(this, true)
        } else {
            PlayerHelper.landscapeMatchWidthHeight(this, videoContainer, null)
            isFullScreen = true
            PreviewVideoPlayer.get()?.setReceiverGroupConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE)
        }
    }

    fun quitFullScreen() {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        isFullScreen = false
    }

//    http://vfx.mtime.cn/Video/2020/09/21/mp4/200921111356067117.mp4
    val mDataSource = MTimeVideoData("http://vfx.mtime.cn/Video/2020/09/21/mp4/200921111356067117.mp4", 0L, 1L, 0L)
    fun playVideo(vid: Long, source: Long, continuePlay: Boolean) {
//        mVid = vid
//        mIntentContinuePlay = continuePlay
//        updateNeedRecommendList()

        val isEqualData: Boolean = PreviewVideoPlayer.get()?.isEqualData(vid)?:false
        val isInPlaybackState: Boolean = PreviewVideoPlayer.get()?.isInPlaybackState()?:false
        if (!isEqualData || !isInPlaybackState || !continuePlay) {
            PreviewVideoPlayer.get()?.play(mDataSource, true)
        }
    }

    override fun onBackPressed() {
        if (isFullScreen) {
            orientationHelper?.toggleScreen()
        } else {
            super.onBackPressed()
        }
    }

    private fun keepOnScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO -> {
            }
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE ->{
                "横竖屏切换".e()
                orientationHelper?.toggleScreen()
            }
            DataInter.ReceiverEvent.EVENT_DANMU_COVER_SEND_DANMU -> {
            }               //全屏状态是弹幕层的弹幕发送请求,交由外层处理
            DataInter.ReceiverEvent.EVENT_CODE_SHARE -> {
                showToast("分享")
//                分享
//                if (onPlayerHolderListener != null) {
//                    onPlayerHolderListener.onShareTo(isFullScreen)
//                }
            }
            DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK -> {
                showToast("反馈错误")
//                if (onPlayerHolderListener != null) {
//                    onPlayerHolderListener.onFeedBack()
//                }
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
        PreviewVideoPlayer.get()?.attachContainer(videoContainer)
        //logic resume player
        //logic resume player
        PreviewVideoPlayer.get()?.logicResourceResume(mDataSource)
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
}