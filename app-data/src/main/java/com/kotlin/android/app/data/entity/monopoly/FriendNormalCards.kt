package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 好友口袋普卡列表（/friendNormalCards.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class FriendNormalCards(
        var cardList: List<Card>? = null, // 卡片列表
        var hitToolCard: HitToolCard? = null // 命中道具卡信息（只涉及黑客卡）
) : ProguardRule