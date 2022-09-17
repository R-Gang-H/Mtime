package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 升级套装（/upgradeSuit.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class UpgradeSuit(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1无效的套装Id，-2无效的套装级别，-3不是当前用户的套装，-4套装数量不足，-5限量套装不能升级，-6已经是终极套装
        var bizMessage: String? = null, // 错误信息
        var suitName: String? = null, // 套装名称
        var suitCover: String? = null, // 套装封面图
        var suitClassText: String? = null, // 套装级别文字
        var rewardToolInfo: RewardPropInfo? = null, // 奖励的道具卡信息
        var rewardGold: Long = 0, // 奖励的金币数
        var relatedId: Long = 0, // 关联对象Id
        var relatedType: Long = 0, // 关联对象类型：1电影 2影人
        var suitDetailLayerUpdateInfo: UpgradeSuitInfo? = null, // 升级套装后套装详情浮层需要更新的信息（应app开发要求新建的字段，也可以通过调用suitDetail.api整体刷新套装详情浮层）
) : ProguardRule