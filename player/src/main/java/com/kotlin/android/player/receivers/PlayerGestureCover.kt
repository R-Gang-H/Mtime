package com.kotlin.android.player.receivers

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.touch.OnTouchGestureListener
import com.kk.taurus.playerbase.utils.TimeUtil
import com.kotlin.android.ktx.ext.core.audioManager
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.R

/**
 * create by lushan on 2020/8/31
 * description:播放器手势控制
 */
class PlayerGestureCover(var ctx: Context) : BaseCover(ctx), OnTouchGestureListener {
    private val TAG = "PlayerGestureCover"

    private val MAX_FORWARD_MS = 3 * 60 * 1000
    private var mWidth = 0
    private var mHeight: Int = 0
    private var mBrightnessBox: View? = null//亮度控件
    private var mVolumeBox: View? = null//声音
    private var mFastForwardBox: View? = null
    private var mBrightTips: TextView? = null
    private var mSeekTips: TextView? = null
    private var mVolumeProgress: ProgressBar? = null
    private var mSeekProgress: ProgressBar? = null

    private var audioManager: AudioManager? = null
    private var mMaxVolume = 0

    private var firstTouch = false
    private var volume = 0
    private val mGestureEnable = true

    private var horizontalSlide = false
    private var rightVerticalSlide = false

    private var mTimerUpdateEnable = true
    private var mBundle: Bundle? = null

    private var newPosition: Long = -1
    private var brightness = -1f
    private var currentBrightness = -1f
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> notifyMeasure()
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
            }
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.layout_gesture_cover, null)
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        mBrightnessBox = findViewById(R.id.brightness_root)
        mBrightTips = findViewById(R.id.app_video_brightness)
        mSeekTips = findViewById(R.id.tv_current)
        mVolumeBox = findViewById(R.id.volume_root)
        mVolumeProgress = findViewById(R.id.volume_progressbar)
        mSeekProgress = findViewById(R.id.duration_progressbar)
        mFastForwardBox = findViewById(R.id.fast_forward_root)
        ShapeExt.setShapeCorner2Color(mFastForwardBox,R.color.color_4c000000,6)
        ShapeExt.setShapeCorner2Color(findViewById<View>(R.id.volume_ll),R.color.color_4c000000,6)

        mBundle = BundlePool.obtain()

        initAudioManager(context)
    }

    private fun initAudioManager(context: Context?) {
        audioManager = context?.audioManager
        mMaxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0
    }


    private fun notifyMeasure() {
        if (mWidth <= 0 || mHeight <= 0) {
            view.post {
                mWidth = view.width
                mHeight = view.height
            }
        }
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        notifyMeasure()
    }

    private fun setVolumeBoxState(state: Boolean) {
        mVolumeBox?.visible(state)
    }

    private fun setBrightnessBoxState(state: Boolean){
        mBrightnessBox?.visible(state)
    }
    private fun setFastForwardState(state: Boolean) {
        mFastForwardBox?.visible(state)
    }

    private fun setFastForwardProgress(newPosition: Int) {
        val duration: Int = getDuration()
        if (duration <= 0) return
        mSeekProgress?.max = getDuration()
        mSeekProgress?.progress = newPosition
    }

    private fun setFastForwardTips(text: String) {
        val duration = getDuration()
        if (duration <= 0) return
        mSeekTips?.text = text
    }
    private fun setBrightnessText(text: String) {
        mBrightTips?.text = text
    }

    private fun updateVolumeProgress(curr: Int, max: Int) {
        mVolumeProgress?.max = max
        mVolumeProgress?.progress = curr
    }


    private fun getDuration(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.duration ?: 0
    }
    override fun getCoverLevel(): Int {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_GESTURE)
    }

    override fun onSingleTapConfirmed(event: MotionEvent?) {

    }

    override fun onLongPress(event: MotionEvent?) {
    }

    override fun onDoubleTap(event: MotionEvent?) {
    }

    override fun onDown(event: MotionEvent?) {
        firstTouch = true
        volume = getVolume()
    }

    private fun getVolume(): Int {
        volume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?:0
        if (volume < 0) volume = 0
        return volume
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {
        if (!mGestureEnable) return
        val mOldX = e1?.x?:0f
        val mOldY = e1?.y?:0f
        val deltaY = mOldY - (e2?.y?:0f)
        val deltaX = mOldX - (e2?.x?:0f)
        if (firstTouch) {
            horizontalSlide = Math.abs(distanceX) >= Math.abs(distanceY)
            rightVerticalSlide = mOldX > mWidth * 0.5f
            firstTouch = false
        }

        if (horizontalSlide) {
            onHorizontalSlide(-deltaX / mWidth)
        } else {
            if (Math.abs(deltaY) > mHeight) return
            if (rightVerticalSlide) {
                onRightVerticalSlide(deltaY / mHeight, distanceX, distanceY)
            } else {
                onLeftVerticalSlide(deltaY / mHeight, distanceX, distanceY)
            }
        }
    }

    private fun getCurrentPosition(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.currentPosition ?: 0
    }
    private fun onHorizontalSlide(percent: Float) {
        Log.d(TAG, "horizontal slide percent = $percent")
        if (getDuration() <= 0) return
        val minW = (mWidth * 0.05f).toInt()
        val slideD = Math.abs((mWidth * percent).toInt())
        if (slideD <= minW) {
            return
        }
        if (mTimerUpdateEnable) {
            groupValue.putBoolean(DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE, false)
            mTimerUpdateEnable = false
        }
        val position: Long = getCurrentPosition().toLong()
        val duration = getDuration().toLong()
        val deltaMax = Math.min(duration, MAX_FORWARD_MS.toLong())
        val delta = (deltaMax * percent).toLong()
        newPosition = delta + position
        if (newPosition > duration) {
            newPosition = duration
        } else if (newPosition <= 0) {
            newPosition = 0
        }
        setVolumeBoxState(false)
        setBrightnessBoxState(false)
        setFastForwardState(true)
        val progressText = TimeUtil.getTime(TimeUtil.TIME_FORMAT_01, newPosition)
        setFastForwardProgress(newPosition.toInt())
        setFastForwardTips(progressText)
        mBundle?.putInt(EventKey.INT_ARG1, newPosition.toInt())
        mBundle?.putInt(EventKey.INT_ARG2, duration.toInt())
        notifyReceiverPrivateEvent(
                DataInter.ReceiverKey.KEY_CONTROLLER_COVER,
                DataInter.ReceiverEvent.EVENT_CODE_UPDATE_SEEK,
                mBundle)
    }

    private fun onRightVerticalSlide(percent: Float, distanceX: Float, distanceY: Float) {
        if (Math.abs(distanceX) > Math.abs(distanceY)) return
        Log.d(TAG, "right slide percent = $percent")
        val maxD = mMaxVolume
        var index = (percent * maxD).toInt() + volume
        if (index > mMaxVolume) index = mMaxVolume else if (index < 0) index = 0
        // 变更声音
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0)
        Log.d(TAG, "volume = $index maxVolume = $mMaxVolume")
        // 显示
        setBrightnessBoxState(false)
        setFastForwardState(false)
        setVolumeBoxState(true)
        updateVolumeProgress(index, mMaxVolume)
    }

    private fun onLeftVerticalSlide(percent: Float, distanceX: Float, distanceY: Float) {
        if (Math.abs(distanceX) > Math.abs(distanceY)) return
        val activity = getActivity() ?: return
        if (brightness < 0) {
            brightness = activity.window.attributes.screenBrightness
            if (brightness <= 0.00f) {
                brightness = 0.50f
            } else if (brightness < 0.01f) {
                brightness = 0.01f
            }
        }
        setVolumeBoxState(false)
        setFastForwardState(false)
        setBrightnessBoxState(true)
        val brightnessValue = setBrightness(brightness + percent)
        setBrightnessText((brightnessValue * 100).toInt().toString() + "%")
        currentBrightness = brightnessValue
    }

    private fun getActivity(): Activity? {
        val context = context
        return if (context is Activity) {
            context
        } else null
    }

    private fun setBrightness(brightness: Float): Float {
        val activity = getActivity() ?: return 0f
        val lpa = activity.window.attributes
        lpa.screenBrightness = brightness
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        activity.window.attributes = lpa
        return lpa.screenBrightness
    }

    override fun onEndGesture() {
        volume = -1
        brightness = -1f
        setVolumeBoxState(false)
        setBrightnessBoxState(false)
        if (newPosition >= 0 && getDuration() > 0) {
            val bundle = BundlePool.obtain()
            bundle.putInt(EventKey.INT_DATA, newPosition.toInt())
            requestSeek(bundle)
            newPosition = -1
        }
        setFastForwardState(false)
//        PlayerHelper.recordBrightness(currentBrightness);
        //        PlayerHelper.recordBrightness(currentBrightness);
        mTimerUpdateEnable = true
    }
}