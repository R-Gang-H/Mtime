package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * @author vivian.wei
 * @date 2020/9/4
 * @desc 单个游戏榜单上榜用户
 */
data class GameRankUser(
    var orderNumber: Long ?= 0,         // 序号
    var reasonDesc: String ?= "",       // 入榜理由
    var rankFluctuation: Long ?= 0,     // 排名波动（正数为上升，负数为下降）
    var rewardGold: Long ?= 0,          // 奖励金币数
    var rewardGoldDesc: String ?= "",   // 奖励金描述
    var userInfo: GameUserInfo ?= null  // 用户信息
): ProguardRule