package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 拥有限量版套装的会员列表（/limitSuitUsers.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class LimitSuitUsers(
        var userCount: Long = 0, // 拥有套装的用户数
        var suitId: Long = 0, // 套装Id
        var suitName: String? = null, // 套装名称
        var suitCover: String? = null, // 套装封面图
        var earliestUser: UserInfo? = null, // 合成最早的用户信息
        var maxinumUser: UserInfo? = null, // 拥有数量最多的用户信息
        var userList: List<UserInfo>? = null // 拥有数量最多的前60个用户列表
) : ProguardRule