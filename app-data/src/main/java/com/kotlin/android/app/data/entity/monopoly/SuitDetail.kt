package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 套装详情弹层（/suitDetail.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SuitDetail(
        var suitId: Long = 0, // 套装Id
        var suitName: String? = null, // 套装名称
        var suitCover: String? = null, // 套装封面图
        var cardCount: Long = 0, // 卡片数量
        var suitClassText: String? = null, // 套装级别文字
        var rewardGold: Long = 0, // 奖励金币数
        var rewardToolCount: Long = 0, // 奖励道具卡数
        var relatedId: Long = 0, // 关联对象Id
        var relatedType: Long = 0, // 关联对象类型：1电影，2影人
        var cardList: List<Card>? = null, // 卡片列表
        var canUpgrade: Boolean = false, // 是否能升级
        var suitCount: Long = 0, // 卡片列表
) : ProguardRule