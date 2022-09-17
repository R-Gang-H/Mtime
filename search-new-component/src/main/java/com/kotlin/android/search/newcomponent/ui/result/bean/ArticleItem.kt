package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * 搜索结果页显示用文章数据
 */
data class ArticleItem(
    var articleId: Long = 0L,               // 文章Id
    var articleTitle: String = "",          // 文章标题
    var image: String = "",                 // 图片
    var authorNickName: String = "",        // 作者名
    val publishTime: Long = 0L              // 发布时间
): ProguardRule

