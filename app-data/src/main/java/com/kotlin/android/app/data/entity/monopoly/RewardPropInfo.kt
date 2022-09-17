package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 奖励的道具卡信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
*/
data class RewardPropInfo(
        var toolId: Long = 0, // 道具卡Id
        var toolCover: String? = null, // 道具卡封面图
        var toolName: String? = null, // 道具卡名称
        var sponsorOrAccepter: Long? = null //发起方1,接收方2 【gameRecord.api用，其他接口忽略此字段
) : ProguardRule