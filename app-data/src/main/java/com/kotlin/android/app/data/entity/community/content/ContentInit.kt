package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2022/4/8
 * des:发布视频类型分类
 **/
data class ContentInit(
    var classifies: ArrayList<Classifies>? = null, // 分类列表
    var fcMovie: FcItem? = null, // 影评电影
    var fcPerson: FcItem? = null, // 影评影人
    var group: FcItem? = null, // 家族
): ProguardRule