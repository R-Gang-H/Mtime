package com.kotlin.android.app.data.entity.community.record

import com.kotlin.android.app.data.annotation.AuthType
import com.kotlin.android.app.data.annotation.Gender
import com.kotlin.android.app.data.ProguardRule

/**
 * 创建人
 *
 * Created on 2020/9/29.
 *
 * @author o.s
 */

data class CreateUser(
        var userId: Long = 0, // 用户ID
        var avatarUrl: String? = null, // 头像
        var nikeName: String? = null, // 用户昵称
        @Gender var gender: Long = 0, // 性别 1:男2:女3:保密
        @AuthType var authType: Long = 0, // 用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
        var followed: Boolean? = null // 当前用户是否关注该用户 true:已关注 false:未关注 null:用户本人
) : ProguardRule