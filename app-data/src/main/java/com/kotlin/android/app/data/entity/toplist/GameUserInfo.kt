package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/9/4
 * @desc 单个游戏榜单上榜用户信息
 */
data class GameUserInfo(
    var userId: Long ?= 0,          // 用户Id
    var nickName: String ?= "",     // 头像
    var avatarUrl: String ?= ""     // 昵称
): ProguardRule