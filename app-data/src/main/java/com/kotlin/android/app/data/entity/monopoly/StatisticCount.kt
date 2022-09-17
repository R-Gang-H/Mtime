package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 个人统计数据（我的主页专用）
 *
 * Created on 2021/7/21.
 *
 * @author o.s
 */
data class StatisticCount(
    var satisfyCount: Long = 0, // 实现愿望数
    var yesterdayGold: Long = 0, // 昨日收入金币数
    var yesterdayToolCard: Long = 0, // 昨日使用道具卡数
    var yesterdayCard: Long = 0, // 昨日收集卡片数
    var yesterdaySuit: Long = 0, // 昨日合成套装数
    var yesterdayBox: Long = 0, // 昨日挖宝箱数
) : ProguardRule
