package com.kotlin.android.player

import android.app.Application
import com.kk.taurus.ijkplayer.IjkPlayer
import com.kk.taurus.playerbase.config.PlayerConfig
import com.kk.taurus.playerbase.config.PlayerLibrary
import com.kk.taurus.playerbase.entity.DecoderPlan
import com.kk.taurus.playerbase.log.PLog

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/15
 *
 * 播放器基础配置类
 */
object PlayerConfig {

    const val PLAN_ID_IJK = 1

    var ignoreMobile = false
    var isListMode = false
    var liveStatus:LiveStatus? = null//直播状态 null不是直播状态，LIVING直播中、L_REVIEWER 直播回放

    @JvmStatic
    fun init(app: Application, debug: Boolean) {
        PLog.LOG_OPEN = false
        PlayerConfig.setUseDefaultNetworkEventProducer(true)
        PlayerConfig.addDecoderPlan(DecoderPlan(PLAN_ID_IJK, IjkPlayer::class.java.name, "ijkplayer"))
        PlayerConfig.setDefaultPlanId(PLAN_ID_IJK)
        PlayerLibrary.init(app)
    }
}
enum class LiveStatus{
    LIVING,//直播中
    L_REVIEWER//直播回放
}