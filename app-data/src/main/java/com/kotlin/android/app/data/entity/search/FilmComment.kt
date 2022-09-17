package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule

/**
 * 联合搜索 影评
 */
data class FilmComment(
    var filmCommentId: Long? = 0L,          // 影评id
    var filmCommentTitle: String?  = "",    // 影评标题
    var createTime: String?  = "",          // 创建时间
    var likeUp: Long? = 0L,                 // 点赞数
    var likeDown: Long? = 0L,               // 点踩数
    var isLikeUp: Boolean? = false,         // 当前用户是否点赞
    var isLikeDown: Boolean? = false        // 当前用户是否点踩
): ProguardRule
