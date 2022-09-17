package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 联合搜索 日志
 */
data class Log(
    var logId: Long? = 0L,              // 日志id
    var logTitle: String?  = "",        // 日志名称
    var content: String?  = "",         // 日志正文
    var createTime: String?  = "",      // 创建时间
    var commentCount: Long? = 0L,       // 评论数
    var likeUp: Long? = 0L,             // 点赞数
    var isLikeUp: Boolean? = false,     // 当前用户是否点赞
    var isLikeDown: Boolean? = false    // 当前用户是否点踩
): ProguardRule
