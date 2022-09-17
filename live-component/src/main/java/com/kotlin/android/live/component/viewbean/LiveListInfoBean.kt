package com.kotlin.android.live.component.viewbean

import com.kotlin.android.app.data.entity.live.LiveInfo
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.live.component.constant.LIVE_HAD_APPOINT
import com.kotlin.android.live.component.constant.LIVE_UN_APPOINT
import com.kotlin.android.mtime.ktx.formatCount

/***
 * 直播列表item数据
 */
data class LiveListInfoBean(
        var appointStatus: Long = 0, //预约状态0：未预约，1：已预约
        var image: String = "",
        var liveId: Long = 0,
        var liveStatus: Long = 0, //直播状态 1-直播前，2-直播中，3-直播结束，4-回放
        var startTime: String = "",
        var statistic: String = "0", //人数
        var title: String = ""
) : ProguardRule {
    companion object {
        fun converter(info: LiveInfo): LiveListInfoBean {
            return LiveListInfoBean(
            appointStatus = info.appointStatus ?: LIVE_UN_APPOINT,
            image = info.image ?: "",
            liveId = info.liveId ?: 0,
            liveStatus = info.liveStatus ?: 1,
            startTime = if(null != info.startTime && info.startTime!! > 0)
                TimeExt.millis2String(info.startTime!!, "MM月dd日 HH:mm") else "",
            statistic = formatCount(info.statistic ?: 0),
            title = info.title ?: ""
            )
        }
    }

    fun isAppoint(): Boolean {
        return appointStatus == LIVE_HAD_APPOINT
    }
}