package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 商店宝箱
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Box(
        var cardBoxId: Long? = 0, // 宝箱Id
        var cardBoxName: String? = null, // 宝箱名称
        var cover: String? = null, // 宝箱封面图
        var activityEndTime: Long? = 0, // 活动结束时间（活动宝箱会用到）
        var price: Long? = 0, // 价格（金币数）
        var description: String? = null, // 描述信息
        var remainCount: Long = 0, // 剩余数量

        var unlockTime: Long = 0, // 解锁时间（时间戳，单位秒）
        var position: Int = 0, // 宝箱位置：1，2，3，4

        var openGold: Long = 0, // 打开宝箱所需金币数

        var type: Int = 0 // 自定义（item类型）
) : ProguardRule