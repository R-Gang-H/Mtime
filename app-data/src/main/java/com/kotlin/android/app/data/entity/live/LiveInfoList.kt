package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

data class LiveInfoList(
        var livePreviews: List<LiveInfo>? = null,//直播前
        var livings: List<LiveInfo>? = null,//直播中
        var wonderVods: List<LiveInfo>? = null//回放
) : ProguardRule