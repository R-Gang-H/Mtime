package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 合成套装（/mixSuit.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class MixSuit(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1无效的卡片Id，-2无效的卡片数量，-3不是当前用户的卡片，-4卡片不在固定口袋里，-5卡片不属于同一套装，-6当前用户命中黑客卡
        var bizMessage: String? = null, // 错误提示信息
        var suitName: String? = null, // 合成套装名称
        var suitCover: String? = null, // 套装封面图
        var rewardGold: Long = 0, // 奖励的金币数
        var rewardGoldDetail: RewardGoldDetail? = null, // 奖励的金币数详情
        var rewardToolInfo: RewardPropInfo? = null, // 奖励的道具卡信息
        var cardList: List<Card>? = null, // 合成套装的卡片列表
        var pocketCards: PocketCards? = null, // 固定口袋卡片信息（刷新列表）
        var relatedId: Long = 0, // 关联对象Id
        var relatedType: Long = 0 // 关联对象类型：1电影 2影人
) : ProguardRule