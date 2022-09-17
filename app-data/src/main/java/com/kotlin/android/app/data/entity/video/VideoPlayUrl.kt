package com.kotlin.android.app.data.entity.video

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2020/9/3
 * description:视频播放地址
 */
class VideoPlayUrl(
        var fileSize: Long = 0L,//文件大小
        var url: String? = "",//播放地址
        var name: String? = "",//视频清晰度名称
        var resolutionType: Long = 0L,//分辨率类型
        var isLive: Boolean = false
) : ProguardRule, Serializable