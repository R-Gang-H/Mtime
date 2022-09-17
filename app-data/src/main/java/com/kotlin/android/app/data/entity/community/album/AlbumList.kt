package com.kotlin.android.app.data.entity.community.album

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/23
 * description:分页获取相册信息
 */
data class AlbumList(
    var items: List<AlbumItem>? = mutableListOf(),//相册信息
    var totalCount: Long = 0L//总记录数
): ProguardRule {
    data class AlbumItem(
            var commentCount: Long = 0L,//评论数
            var coverUserImageId: Long = 0L,//相册封面选用的图片ID，如果为0，则为默认相册封面
            var coverUserImageUrl: String? = "",//相册封面选用的图片Url，如果为0，则为默认相册封面
            var description: String? = "",//相册描述
            var enterUser: Long = 0L,//相册所有人id
            var id: Long = 0L,//相册id
            var memory: Long = 0L,//相册目前占用空间（单位：字节）
            var name: String? = "",//相册名
            var photoNumber: Long = 0L,//相册中的照片数量，缺省为0
            var praiseCount: Long = 0L//点赞数
    ): ProguardRule
}

