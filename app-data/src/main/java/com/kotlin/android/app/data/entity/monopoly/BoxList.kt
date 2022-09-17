package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 商店宝箱列表（/cardBoxList.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class BoxList(
        var boughtBox: Box? = null, // 当前用户还未领取的宝箱奖励
        var commonBoxList: List<Box>? = null, // 普通宝箱列表
        var activityBoxList: List<Box>? = null, // 活动宝箱列表
        var limitBoxList: List<Box>? = null // 限量宝箱列表
) : ProguardRule