package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 从好友/机器人的开放口袋拾取卡片（/pickCardFromFriend.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class PickCard(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1不是好友关系，-2不是好友/机器人的卡片，-3卡片不在好友/机器人的开放口袋里，-4我的固定口袋没有空位
        var bizMessage: String? = null, // 错误提示信息
        var openPocketCards: PocketCards? = null // 好友的开放口袋卡片信息（刷新列表）
) : ProguardRule