package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * 直播资讯
 */
data class LiveNews(
        var img: String? = "",      // 单图（后台上传）
        var contentType: Long = 0L, // 关联资讯类型：1：文章 2：帖子。 根据type确定跳转到何种相应的详情页
        var contentId: Long = 0L,   // Long型ID
        var tag: String? = "",      // 资讯标签
        var title: String? = ""     // 资讯标题
): ProguardRule {
    companion object {
        const val CONTENT_TYPE_ARTICLE = 1L
        const val CONTENT_TYPE_POST = 2L
    }
}
