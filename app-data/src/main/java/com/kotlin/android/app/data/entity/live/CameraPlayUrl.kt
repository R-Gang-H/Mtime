package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2021/3/9
 * description:直播机位播放地址
 */
data class CameraPlayUrl(
        var hd: PlayUrl? = null,//高清
        var ori: PlayUrl? = null,//原画
        var sd: PlayUrl? = null//标清
) : ProguardRule {
    data class PlayUrl(
            var flvUrl: String? = "",
            var hlsUrl: String? = "",
            var rtmpUrl: String? = ""
    ) : ProguardRule
}

