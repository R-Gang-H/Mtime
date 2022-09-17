package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * 直播资讯列表
 */
data class LiveNewsList(
        var news: MutableList<LiveNews>? = null // 直播资讯列表
): ProguardRule
