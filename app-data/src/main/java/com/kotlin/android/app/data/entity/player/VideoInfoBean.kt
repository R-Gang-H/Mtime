package com.kotlin.android.app.data.entity.player

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2020/8/12
 * description:
 */
data class VideoInfoBean(
        var videoInfoList: MutableList<VideoInfoItem>? = mutableListOf()
) : ProguardRule, Serializable {
    data class VideoInfoItem(
            var videoId: Long = 0L,
            var title: String? = "",
            var playLength: Int = 0,
            var logo: String? = "",
            var playable: Boolean = false
    ) : ProguardRule, Serializable

    fun getTitle(): String? {
        return if (videoInfoList?.isNotEmpty() == true) {
            videoInfoList?.get(0)?.title.orEmpty()
        } else ""
    }
}