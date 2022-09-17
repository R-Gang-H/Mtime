package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 联合搜索 帖子
 */
data class Post(
    var postId: Long? = 0L,             // 帖子Id
    var postTitle: String?  = "",       // 帖子标题
    var createTime: String?  = "",      // 创建时间
    var commentCount: Long? = 0L,       // 评论数
    var likeUp: Long? = 0L,             // 点赞数
    var isLikeUp: Boolean? = false,     // 当前用户是否点赞
    var isLikeDown: Boolean? = false    // 当前用户是否点踩
): ProguardRule
