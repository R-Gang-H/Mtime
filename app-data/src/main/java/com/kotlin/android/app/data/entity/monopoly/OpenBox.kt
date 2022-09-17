package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 打开宝箱（/openBox.api）
 * bizCode: 业务返回码：1成功，0失败，-1无效的位置值，-2当前位置没有宝箱，-3宝箱未解锁
 *
 * 卡片大富翁api - 购买宝箱（/buyCardBox.api）
 * bizCode: 业务返回码：1成功，0失败，-1还有未领取的宝箱奖励，-2无效的宝箱Id，-3库存不足，-4限量宝箱不能购买，-5限时活动已结束
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class OpenBox(
        var bizCode: Long = 0, //
        var bizMessage: String? = null, //
        var rewardInfo: RewardInfo? = null // 宝箱奖励信息
) : ProguardRule