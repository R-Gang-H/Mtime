package com.kotlin.android.app.data.entity.user

import com.kotlin.android.app.data.ProguardRule

/**
 * Item User
 * [Login]
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
data class ItemUser(
        val userId: Long = 0,
        val nickname: String? = null, // 昵称
        val headImg: String? = null, // 头像地址
        val mobile: String? = null, // 绑定手机号
        val dataEncryption: String? = null, // userId加密
        val gender: Int = 0 // 性别 1 男 、2 女、 3 保密
) : ProguardRule