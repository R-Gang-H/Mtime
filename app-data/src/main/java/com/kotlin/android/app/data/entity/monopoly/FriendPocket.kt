package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * TA的口袋信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class FriendPocket(
        var userInfo: UserInfo? = null, // 当前用户信息
//        var hitToolCard: HitToolCard? = null, // 命中道具卡信息
//        var suitInfo: SuitInfo? = null, // TA的套装信息
        var pocketCards: PocketCards? = null, // 固定口袋卡片信息
        var openPocketCards: PocketCards? = null, // 开放口袋卡片信息
        var friendRelation: Boolean = false, // 是否好友关系
        var scampGold: Long = 0, // 流氓卡金币
        var bufferInfo: BufferInfo? = null, // 拥有道具卡效果信息
        var wishInfo: WishInfo? = null, // 拥有道具卡效果信息
) : ProguardRule