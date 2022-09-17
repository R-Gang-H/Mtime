package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.R

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/15
 *
 * 播放器加载中提示
 */
class LoadingCover(context: Context): BaseCover(context) {
    private val TAG = "LoadingCover"

    private val LOADING_TEXT_TYPE_ON_LOADING = 0
    private val LOADING_TEXT_TYPE_RIGHT_NOW_PLAY = 1

    private lateinit var mLoadingIcon: ImageView
    private lateinit var mLoadingText: TextView
    private lateinit var mLoadingAnimation: RotateAnimation

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.view_player_loading_cover, null)
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        mLoadingIcon = findViewById(R.id.video_layout_player_loading_icon_iv)
        mLoadingText = findViewById(R.id.video_layout_player_loading_hint_tv)
        mLoadingAnimation = AnimationUtils.loadAnimation(context, R.anim.player_anim_loading) as RotateAnimation
        findViewById<View>(R.id.mPlayerLoadingCoverLayout).setBackground(
                colorRes = R.color.color_4c000000,
                cornerRadius = 6.dpF
        )
        mLoadingAnimation.interpolator = LinearInterpolator()
        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    private val onGroupValueUpdateListener: OnGroupValueUpdateListener = object : OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_ERROR_SHOW_STATE)
        }

        override fun onValueUpdate(s: String, o: Any) {
            if (DataInter.Key.KEY_ERROR_SHOW_STATE == s) {
                if (o as Boolean) {
                    setLoadingState(false)
                }
            }
        }
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START -> {
                LogUtils.d(TAG, "on url loading......")
                switchLoadingText(LOADING_TEXT_TYPE_RIGHT_NOW_PLAY)
                setLoadingState(true)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {
                LogUtils.d(TAG, "on set data source......")
                switchLoadingText(LOADING_TEXT_TYPE_RIGHT_NOW_PLAY)
                setLoadingState(true)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
                LogUtils.d(TAG, "on render start......")
                setLoadingState(false)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO -> {
                switchLoadingText(LOADING_TEXT_TYPE_ON_LOADING)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START -> {
                LogUtils.d(TAG, "buffering start......")
                switchLoadingText(LOADING_TEXT_TYPE_ON_LOADING)
                setLoadingState(true)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END -> {
                LogUtils.d(TAG, "buffering end......")
                setLoadingState(false)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_STOP -> {
                LogUtils.d(TAG, "on stopped......")
                setLoadingState(false)
            }
        }
    }

    override fun onErrorEvent(i: Int, bundle: Bundle?) {}

    override fun onReceiverEvent(i: Int, bundle: Bundle?) {}

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        mLoadingIcon.clearAnimation()
        mLoadingIcon.post { mLoadingIcon.startAnimation(mLoadingAnimation) }
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        mLoadingIcon.clearAnimation()
        setLoadingState(false)
    }

    private fun isErrorState(): Boolean {
        return groupValue != null && groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE)
    }

    fun setLoadingState(state: Boolean) {
        if (isErrorState()) {
            setCoverVisibility(View.GONE)
            return
        }
        setCoverVisibility(if (state) View.VISIBLE else View.GONE)
    }

    private fun switchLoadingText(type: Int) {
        when (type) {
            LOADING_TEXT_TYPE_RIGHT_NOW_PLAY -> setLoadingText(getString(R.string.player_loading_play_hint))
            LOADING_TEXT_TYPE_ON_LOADING -> setLoadingText(getString(R.string.player_loading_text))
        }
    }

    private fun getString(id: Int): String? {
        return context.getString(id)
    }

    fun setLoadingText(text: String?) {
        mLoadingText.text = text
    }

    override fun getCoverLevel(): Int {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_LOADING)
    }

}