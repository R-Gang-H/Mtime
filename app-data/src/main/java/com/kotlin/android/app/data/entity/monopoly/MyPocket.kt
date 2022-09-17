package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 我的口袋信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class MyPocket(
        var userInfo: UserInfo? = null, // 当前用户信息
//        var hitToolCard: HitToolCard? = null, // 命中道具卡信息
//        var suitInfo: SuitInfo? = null, // 我的套装信息
        var pocketCards: PocketCards? = null, // 固定口袋卡片信息
        var openPocketCards: PocketCards? = null, // 开放口袋卡片信息
        var strongBoxInfo: StrongBoxPositionList? = null, // 宝箱空位列表
        var cardBoxList: List<Box>? = null, // 宝箱列表
        var bufferInfo: BufferInfo? = null, // 拥有道具卡效果信息
        var unreadMsgCount: Long = 0, // 未读消息数
) : ProguardRule