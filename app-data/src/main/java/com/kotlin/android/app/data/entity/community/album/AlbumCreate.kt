package com.kotlin.android.app.data.entity.community.album

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/23
 * description:创建相册
 */
data class AlbumCreate(var id:Long = 0L,//相册id
var result:Boolean = false//保存结果 true成功 false失败
): ProguardRule