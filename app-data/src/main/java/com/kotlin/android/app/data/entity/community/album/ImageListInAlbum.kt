package com.kotlin.android.app.data.entity.community.album

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/23
 * description:分页获取相册中的图片
 */
data class ImageListInAlbum(
        var nextStamp:String? = null,
        var hasNext:Boolean = true,
        var items: MutableList<Image>? = mutableListOf(),//相册中图片
        var totalCount: Long = 0L//总记录数
) : ProguardRule {
    data class Image(
            var fileId:String? = "",//图片文件名
            var fileUrl: String? = "",//图片url
            var id: Long = 0L,//图片id

    ) : ProguardRule
}

