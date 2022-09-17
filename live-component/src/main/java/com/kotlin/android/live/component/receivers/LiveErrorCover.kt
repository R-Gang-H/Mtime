package com.kotlin.android.live.component.receivers

import android.content.Context
import android.graphics.Color
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.receivers.ErrorCover

/**
 * create by lushan on 2021/4/20
 * description:直播播放器错误按钮
 */
class LiveErrorCover(context: Context):ErrorCover(context) {
    override fun setClickListener() {

    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        showErrorTitleLayout(false)
    }
    override fun showError(errorType: Int) {
        super.showError(errorType)
        var bgColor = Color.parseColor("#000000")
        view.setBackgroundColor(bgColor)
    }

    override fun getCoverLevel(): Int {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_LIVE_ERROR)
    }

    override fun checkErrorTitle() {
        setErrorTitleLayoutState(false)
    }
}