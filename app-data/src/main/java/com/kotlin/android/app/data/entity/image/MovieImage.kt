package com.kotlin.android.app.data.entity.image

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/11/18
 * description: 获取电影图片列表（100张剧照+100张海报）(movie/image.api
 */
data class MovieImage(var imageInfos: MutableList<ImageInfo>? = null) : ProguardRule {

    data class ImageInfo(var id: Long? = 0L,
                         var image: String? = "",
                         var type: Long? = 0L) : ProguardRule
}