package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 从我的开放口袋拾取卡片（/pickCardFromMe.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class PickCardFromMe(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1不是我的卡片，-2卡片不在我的开放口袋里，-3我的固定口袋没有空位，-4我已命中黑客卡
        var bizMessage: String? = null, // 错误提示信息
        var openPocketCards: PocketCards? = null, // 我的开放口袋卡片信息（刷新列表）
        var pocketCards: PocketCards? = null // 我的固定口袋卡片信息（刷新列表）
) : ProguardRule {
    fun isSuccess() = bizCode == 1L
}