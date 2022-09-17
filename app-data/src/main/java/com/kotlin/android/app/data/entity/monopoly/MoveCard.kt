package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 移动卡片到固定口袋
 * 业务返回码：1成功，0失败，-1不是我的卡片，-2卡片不在保险箱内，-3固定口袋没有空位
 * 卡片大富翁api - 移动卡片到保险箱（/moveCardToStrongBox.api）
 * 业务返回码：1成功，0失败，-1不是我的卡片，-2卡片不在固定口袋里，-3保险箱没有空位，-4当前用户命中黑客卡
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class MoveCard(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1不是我的卡片，-2卡片不在保险箱内，-3固定口袋没有空位
        var bizMessage: String? = null, // 错误提示信息
        var pocketCards: PocketCards? = null, // 固定口袋卡片信息（刷新列表）
        var strongBoxInfo: StrongBoxPositionList? = null // 保险箱信息（刷新列表）
) : ProguardRule