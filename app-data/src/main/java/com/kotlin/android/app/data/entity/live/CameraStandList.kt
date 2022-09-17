package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2021/3/9
 * description:直播机位
 */
data class CameraStandList(
    var cameraList: MutableList<Camera>? = mutableListOf()
): ProguardRule {
    data class Camera(
            var hightUrl: String?= "",//高清流
            var img: String? = "",//机位封面图
            var length: Long = 0L,//时长
            var title: String? = "",//机位名称
            var url: String? = "",//标清url
            var videoId: Long = 0L//机位id
    ): ProguardRule
}

