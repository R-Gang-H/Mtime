package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 道具卡
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class PropCard(
        var toolId: Long = 0, // 道具卡Id
        var toolName: String? = null, // 道具卡名称
        var toolCover: String? = null, // 道具卡封面图
        var description: String? = null, // 描述信息
        var price: Long = 0, // 价格（金币数）
        var todaySaleCount: Long = 0, // 今日售出数
        var useType: Long = 0, // 使用类型：1对自己使用，2对好友使用，3不能直接使用
        var remainCount: Long = 0, // 剩余数量
        var buy:Boolean? = true,//是否可以购买
        var buyNum:Long = 0 //购买数量
) : ProguardRule