package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * 搜索结果页显示用影评数据
 */
data class FilmCommentItem(
    var filmCommentId: Long = 0L,          // 影评id
    var filmCommentTitle: String  = "",    // 影评标题
    var likeUp: Long = 0L,                 // 点赞数
    var likeDown: Long = 0L,               // 点踩数
    var isLikeUp: Boolean = false,         // 当前用户是否点赞
    var isLikeDown: Boolean = false        // 当前用户是否点踩
): ProguardRule
