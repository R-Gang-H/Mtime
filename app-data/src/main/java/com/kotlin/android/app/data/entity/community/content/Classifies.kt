package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.ProguardRule

data class Classifies(
    var key: Long = 0L,//分类类型
    var value: String? = ""//分类名称
) : ProguardRule