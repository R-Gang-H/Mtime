package com.kotlin.android.app.data.entity.user

import com.kotlin.android.app.data.ProguardRule

/**
 * 直播答题用户信息
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
data class LiveAnswerUser(
        val uid: Long = 0L,
        val nickname: String? = null, // 用户名
        val avatarUrlPic: String? = null // 用户头像
) : ProguardRule