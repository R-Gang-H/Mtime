package com.kotlin.android.app.data.entity.video

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2020/9/27
 * description: 获取视频播放地址2接口对应实体类 /video/play_url2
 */
data class VideoPlayList(
        var playUrlList:ArrayList<VideoPlayUrl>? = arrayListOf(),
): ProguardRule,Serializable