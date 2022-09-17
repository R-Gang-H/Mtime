package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.R
import com.kotlin.android.player.ReceiverGroupManager

/**
 * create by lushan on 2020/9/1
 * description:新手引导
 */
class UserGuideCover(var ctx: Context):BaseCover(ctx) {
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
    }
    private val mDelayHiddenGuideCover = Runnable { setGuideState(false) }
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        findViewById<View>(R.id.lla_i_know)?.onClick {
            setGuideState(false)
        }
    }

    private fun setGuideState(state: Boolean) {
        setCoverVisibility(if (state) View.VISIBLE else View.GONE)
        if (state) {
            mHandler.removeCallbacks(mDelayHiddenGuideCover)
            mHandler.postDelayed(mDelayHiddenGuideCover, 5000)
        } else {
            ReceiverGroupManager.updateUserGuideIKnow()
            mHandler.removeCallbacks(mDelayHiddenGuideCover)
        }
        groupValue.putBoolean(DataInter.Key.KEY_USER_GUIDE_STATE, state)
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.layout_player_user_guide_cover, null)
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        setGuideState(true)
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        mHandler.removeCallbacks(mDelayHiddenGuideCover)
        groupValue.putBoolean(DataInter.Key.KEY_USER_GUIDE_STATE, false)
    }

    override fun getCoverLevel(): Int {
        return levelHigh(DataInter.CoverLevel.COVER_LEVEL_USER_GUIDE)
    }
}