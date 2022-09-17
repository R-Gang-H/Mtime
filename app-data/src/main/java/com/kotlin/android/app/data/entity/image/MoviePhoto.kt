package com.kotlin.android.app.data.entity.image

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/11/4
 * description: 影评分享获取影评数据
 */
data class MoviePhoto(var images: MutableList<Image>? = mutableListOf()) : ProguardRule {
    data class Image(var id: Long? = 0L,//图片id
                     var image: String? = "",//图片地址
                     var type: Long? = 0L//图片类型
    ) : ProguardRule
}