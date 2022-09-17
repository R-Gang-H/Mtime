package com.kotlin.android.app.data.entity.movie

import com.kotlin.android.app.data.ProguardRule

/**
 * MovieController - 获取当前用户最新一条长短影评(movie/currentUser/latestComment.api)
 */
data class LatestComment(
        val longComment: Comment? = null, // 长影评实体
        val shortComment: Comment? = null, // 短影评实体
        val userInfo: UserInfo? = null // 当前用户信息实体
) : ProguardRule