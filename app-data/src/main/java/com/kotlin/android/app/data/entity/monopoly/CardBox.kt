package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片宝箱：铜、银、金、铂金、钻石、限量、活动宝箱
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class CardBox(
        var cardBoxId: Long = 0, // 宝箱Id
        var cardBoxName: String? = null, // 宝箱名称
        var cover: String? = null, // 宝箱封面图
        var unlockTime: Long = 0, // 解锁时间（时间戳，单位秒）
        var position: Int = 0 // 宝箱位置：1，2，3，4
) : ProguardRule