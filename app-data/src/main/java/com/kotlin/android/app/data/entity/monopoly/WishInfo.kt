package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 许愿信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class WishInfo(
        var userInfo: UserInfo? = null, // 实现愿望的用户信息
        var cardInfo: Card? = null, // 许愿卡片信息
        var wishId: Long = 0, // 许愿Id
        var content: String? = null, // 许愿内容
        var status: Long = 0 // 许愿状态：1已发布，2已实现
) : ProguardRule