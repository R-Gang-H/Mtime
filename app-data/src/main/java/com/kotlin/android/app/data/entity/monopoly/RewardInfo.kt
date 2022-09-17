package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 宝箱奖励
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class RewardInfo(
        var gold: Long = 0, // 奖励金币数
        var consumeGold: Long = 0, // 消费金币数
        var cardList: List<Card>? = null, // 奖励卡片列表（已废弃）
        var limitCardList: List<Card>? = null, // 限量卡片列表
        var commonCardList: List<Card>? = null, // 普通卡片列表

        var position: Int = 0, // 自定义：开宝箱的位置
) : ProguardRule