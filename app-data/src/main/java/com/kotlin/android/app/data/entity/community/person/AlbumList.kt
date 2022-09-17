package com.kotlin.android.app.data.entity.community.person

import com.kotlin.android.app.data.ProguardRule

/**
 * create by wangwei on 2020/10/21
 * description:分页获取相册信息
 */
data class AlbumList(
    var items: List<AlbumItem>? = mutableListOf(),//相册信息
    var nextStamp:String? = "",
    var hasNext:Boolean = false,
    var totalCount: Long = 0L//总记录数
): ProguardRule {
    data class AlbumItem(
            var id: Long = 0L,//相册id
            var name: String? = "",//相册名
            var description: String? = "",//相册描述
            var photoNumber: Long = 0L,//相册中的照片数量，缺省为0
            var firstFileUrl: String? = "",//http://img2.mtime.cn/up/A2BAAFF4-0FA7-47AB-80CB-1461ABC3D022_o.jpg
            var lastFileUrl: String? = "",//http://img5.mtime.cn/up/2020/10/21/135333.14944566_o.jpg
            var enterTime: String? = "",//2009-05-12 05:19:53
            var lastUpdateTime: String? = "",//2020-10-21 13:53:35
            var memory: Long = 0L//相册目前占用空间（单位：字节）
    ): ProguardRule
}

