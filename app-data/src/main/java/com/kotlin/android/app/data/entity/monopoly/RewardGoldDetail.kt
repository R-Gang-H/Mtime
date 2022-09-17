package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 奖励的金币详情信息
 *
 * Created on 2021/2/26.
 *
 * @author o.s
*/
data class RewardGoldDetail(
        var mixGold: Long = 0, // 合成奖励金币数
        var mammonGold: Long = 0, // 财神卡奖励金币数
        var slaveGold: Long = 0 // 奴隶卡扣减金币数
) : ProguardRule