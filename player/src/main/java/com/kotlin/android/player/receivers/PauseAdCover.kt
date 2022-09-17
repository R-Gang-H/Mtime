package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kotlin.android.app.data.entity.player.VideoAdBean
import com.kotlin.android.app.data.entity.player.VideoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.R
import com.kotlin.android.player.ad.AdHelper
import java.util.*

/**
 * create by lushan on 2020/8/31
 * description: 暂停广告控制层
 */
class PauseAdCover(var ctx: Context) : BaseCover(ctx) {

    private val TAG = "PauseAdCover"
    private var mAdTag: TextView? = null
    private var mAdImage: ImageView? = null
    private var mCloseIcon: ImageView? = null
    private var mAdContainer: RelativeLayout? = null
    private var isAdShow = false
    private var isFullScreen = false

    private var mIndex = 0

    private var mPauseCount = 0
    private var needLargeDelay = false

    private val rate_w = 0.6f
    private val rate_h = 0.61f

    private var image_w = 0
    private var image_h = 0
    private val MSG_CODE_SHOW_IMAGE = 333
    private var isEditDanmu = false
    private var mAdItems: List<VideoAdBean.AdItem> = ArrayList<VideoAdBean.AdItem>()
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_SHOW_IMAGE -> {
                    val item: VideoAdBean.AdItem = msg.obj as VideoAdBean.AdItem
                    Log.d(TAG, "id = " + item.aID + " url = " + item.image)
                    //display image
                    mAdImage?.loadImage(
                        data = item.image.orEmpty(),
                        width = image_w,
                        height = image_h,
                        defaultImgRes = R.drawable.collections_default
                    )
                    val bundle = Bundle()
                    bundle.putInt(EventKey.INT_ARG1, item.aID.toInt())
                    bundle.putInt(EventKey.INT_ARG2, mIndex + 1)
                    //notify show event for statistics
                    notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_ON_PAUSE_AD_SHOW, bundle)
                    //handle applink for ad image
                    val appLink: String = item.applinkData.orEmpty()
                    Log.d(TAG, "applink = $appLink")
                    mAdImage?.setOnClickListener { //notify ad click event for statistics
                        notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_ON_PAUSE_AD_CLICK, bundle)
                        //todo applinkManager如何处理
//                        ApplinkManager.jump(context, appLink, getPageRefer())
                    }

                    //handle ad tag
                    mAdTag?.gone()
                    if (item.isShowTag) {
                        Log.d(TAG, "showTag ..." + item.tagDesc)
                        mAdTag?.visible()
                        mAdTag?.text = item.tagDesc.orEmpty()
                    }

                    //delay show next ad item
                    mIndex++
                    val size: Int = mAdItems.size
                    mIndex %= size
                    val message = Message.obtain()
                    message.what = MSG_CODE_SHOW_IMAGE
                    message.obj = mAdItems[mIndex]

                    //default use item delay duration
                    var delayTime: Long = item.duration * 1000.toLong()
                    if (mPauseCount > 0 && needLargeDelay) {
                        needLargeDelay = false
                        delayTime *= 3
                    }

                    //loop next item
                    sendMessageDelayed(message, delayTime)
                }
            }
        }
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_RESUME -> setAdState(false)
            OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE -> if (!isDanmuCoverInEditState()) {
                setAdState(true)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START, OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> if (isAdShow) {
                setAdState(false)
            }
        }
    }

    private fun isDanmuCoverInEditState(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE)
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.layout_pause_ad_cover, null)
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        mAdTag = findViewById(R.id.layout_pause_ad_cover_tag_tv)
        mAdContainer = findViewById(R.id.layout_pause_ad_cover_container_rl)
        mAdImage = findViewById(R.id.iv_ad_img)
        mCloseIcon = findViewById(R.id.iv_close_ad)
        mAdTag?.apply {
            ShapeExt.setShapeCorner2Color2Stroke(this, R.color.color_66000000, 0, R.color.color_ffffff, 1, false)
        }
        mCloseIcon?.setOnClickListener(View.OnClickListener {
            if (isAdShow) {
                //hidden ad
                setAdState(false)
            }
        })

        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    private fun getCurrentVideoInfo(): VideoInfo? {
        return groupValue.get<VideoInfo>(DataInter.Key.KEY_VIDEO_INFO)
    }

    private fun isAdDataAvailable(): Boolean {
        val videoInfo: VideoInfo = getCurrentVideoInfo() ?: return false
        val adList: List<VideoAdBean.AdItem>? = AdHelper.getAdList(videoInfo.vid, videoInfo.sourceType)
        if (adList == null || adList.isEmpty()) return false
        mAdItems = adList
        return true
    }

    private val onGroupValueUpdateListener: OnGroupValueUpdateListener = object : OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(
                    DataInter.Key.KEY_IS_FULL_SCREEN,
                    DataInter.Key.KEY_IS_LANDSCAPE_EDIT_DANMU
            )
        }

        override fun onValueUpdate(s: String, o: Any) {
            if (DataInter.Key.KEY_IS_FULL_SCREEN == s) {
                isFullScreen = o as Boolean
                setAdShowMode(isFullScreen)
            } else if (DataInter.Key.KEY_IS_LANDSCAPE_EDIT_DANMU == s) {
                isEditDanmu = o as Boolean
            }
        }
    }

    /**
     * 大小屏切换时，动态调整广告图的大小
     * @param isFullScreen
     */
    private fun setAdShowMode(isFullScreen: Boolean) {
        val screen_w: Int = screenWidth
        val screen_h: Int = screenHeight
        val layoutParams = mAdContainer?.layoutParams
        if (isFullScreen) {
            val w = screen_w.coerceAtLeast(screen_h)
            val h = screen_w.coerceAtMost(screen_h)
            image_w = (w * rate_w).toInt()
            image_h = (h * rate_h).toInt()
        } else {
            image_w = (screen_w * rate_w).toInt()
            image_h = (screen_w * 9 / 16 * rate_h).toInt()
        }
        layoutParams?.width = image_w
        layoutParams?.height = image_h
        mAdContainer?.layoutParams = layoutParams
    }

    fun setAdState(state: Boolean) {
        if (!state) {
            mHandler.removeMessages(MSG_CODE_SHOW_IMAGE)
        }
        if (state && !isAdDataAvailable()) {
            return
        }
        if (state && isEditDanmu) {
            setCoverVisibility(View.GONE)
            return
        }
        isAdShow = state
        if (isAdShow) {
            handleDisPlayImage()
        }
        setAdShowMode(isFullScreen)
        setCoverVisibility(if (state) View.VISIBLE else View.GONE)
        if (!state) {
            mAdImage?.setImageResource(R.drawable.collections_default)
        }
    }

    /**
     * 需求：广告内容为2以上时，每次暂停时按照权重顺序轮换展示,当停留时间过长时（界定条件：后台配置的 多张轮播时间*3），每则广告循环播放3次，继续播放下一则广告，依次循环
     *
     * 假设：
     *
     * 广告A  权重：1  多张轮播时间：10s
     *
     * 广告B  权重：2  多张轮播时间：15s
     *
     * 广告C  权重：3  多张轮播时间：20s
     *
     * 1、视频第一次暂停，显示广告A，当停留时间超过10s*3，则显示广告B
     *
     * 2、视频继续播放，第二次暂停，停留时间超过15s*3，则显示广告C
     *
     * 3、视频继续播放，第三次暂停，停留时间超过20s*3，则显示广告A
     */
    private fun handleDisPlayImage() {
        if (isAdDataAvailable()) {
            mIndex = 0
            var adItem: VideoAdBean.AdItem = mAdItems[mIndex]
            val size: Int = mAdItems.size
            if (size >= 2) {
                val tempIndex = mPauseCount % size
                adItem = mAdItems.get(tempIndex)
                mIndex = tempIndex
                mPauseCount++
                needLargeDelay = true
            } else {
                mPauseCount = 0
            }
            val message = Message.obtain()
            message.what = MSG_CODE_SHOW_IMAGE
            message.obj = adItem
            mHandler.sendMessage(message)
        }
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        mHandler.removeMessages(MSG_CODE_SHOW_IMAGE)
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun getCoverLevel(): Int {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_PAUSE_AD)
    }

}