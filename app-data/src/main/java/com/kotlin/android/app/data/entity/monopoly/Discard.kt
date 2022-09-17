package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 丢弃卡片给好友/机器人
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class Discard(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1不是我的卡片，-2卡片不在我的固定口袋里，-3不是好友关系，-4无效的机器人Id，-5好友/机器人的开放口袋没有空位，-6我已命中黑客卡
        var bizMessage: String? = null, // 错误提示信息
        var pocketCards: PocketCards? = null, // 我的口袋卡片信息（刷新列表）
        var openPocketCards: PocketCards? = null // 好友的开放口袋卡片信息（刷新列表）
) : ProguardRule