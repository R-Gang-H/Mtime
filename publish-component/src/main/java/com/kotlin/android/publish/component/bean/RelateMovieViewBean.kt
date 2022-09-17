package com.kotlin.android.publish.component.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/3/30
 * des:视频发布-相关电影/选择分类
 **/
data class RelateMovieViewBean(
    var id:Long = 0L,
    var name: String = "",
    var isSelected:Boolean = false//是否选中，选择分类用到
) : ProguardRule