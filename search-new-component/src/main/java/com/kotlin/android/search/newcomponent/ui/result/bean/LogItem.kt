package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * 搜索结果页显示用日志数据
 */
data class LogItem(
    var logId: Long = 0L,              // 日志id
    var logTitle: String  = "",        // 日志名称
    var commentCount: Long = 0L,       // 评论数
    var likeUp: Long = 0L,             // 点赞数
    var isLikeUp: Boolean = false,     // 当前用户是否点赞
): ProguardRule

