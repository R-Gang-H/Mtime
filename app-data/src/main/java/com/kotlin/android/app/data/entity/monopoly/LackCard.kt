package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 我缺少的卡片
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class LackCard(
        var cardId: Long = 0, // 卡片Id
        var cardName: String? = null, // 卡片封面图
        var cardCover: String? = null, // 卡片封面图
        var ownedFriends: List<UserInfo>? = null // 拥有卡片的好友列表
) : ProguardRule