package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.VideoSource
import com.kotlin.android.app.data.ProguardRule

/**
 * 视频集合 目前无用
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */
data class Videos(
        var videoId: Long = 0, // 视频ID 必填
        var posterUrl: String? = null, // 视频封面图url
        @VideoSource var videoSource: Long = 0, // 视频来源 必填 MOVIE_VIDEO(1, "电影预告片"), SELF_MEDIA_VIDEO(2, "自媒体"), MEDIA_VIDEO(3, "媒资")
        var videoUrl:String? = ""//原视频url,临时使用必填
) : ProguardRule