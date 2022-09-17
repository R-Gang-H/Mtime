package com.kotlin.android.card.monopoly.entity

import com.kotlin.android.app.data.entity.monopoly.UserInfo

/**
 * 缺失卡列表：缺失卡片，对应2个拥有卡片的好友
 *
 * Created on 2020/9/16.
 *
 * @author o.s
 */
data class LackCardViewBean(
        var type: Int = 0, // 类型，1：表示卡，0表示拥有卡的好友
        val cardSuitId: Long = 0,
        val cardId: Long = 0,
        val cardName: String = "",
        val cardCover: String? = null,
        val friend1: UserInfo? = null,
        val friend2: UserInfo? = null,
) {
    /**
    "friendId": 9592,
    "avatarUrl": "0vDCT",
    "nickName": "xAPti",
    "isOnline": false
     */
    data class Friend(
            val friendId: Long,
            val avatarUrl: String? = null,
            val nickName: String? = null,
            val isOnline: Boolean = false
    )
}