package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * 搜索结果页显示用帖子、日志共用数据
 */
data class PostItem(
    var objId: Long = 0L,              // Id
    var objTitle: String  = "",        // 标题
    var objType: Long = 0L,            // 类型
    var commentCount: Long = 0L,       // 评论数
    var likeUp: Long = 0L,             // 点赞数
    var isLikeUp: Boolean = false      // 当前用户是否点赞
): ProguardRule
