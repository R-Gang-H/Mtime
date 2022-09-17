package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

data class LiveInfo(
    var appointStatus: Long? = 0, //预约状态0：未预约，1：已预约
    var image: String? = "",
    var liveId: Long? = 0,
    var liveStatus: Long? = 0, //直播状态 1-直播前，2-直播中，3-直播结束，4-回放
    var startTime: Long? = 0,
    var statistic: Long? = 0, //人数
    var title: String? = ""
) : ProguardRule