package com.kotlin.android.comment.component.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/7
 * description:详情中点赞、点踩、收藏结果类
 */
data class DetailBaseExtend<T>(var result:Any,
var extend:T): ProguardRule