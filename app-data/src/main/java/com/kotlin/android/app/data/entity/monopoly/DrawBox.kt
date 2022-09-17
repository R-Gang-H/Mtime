package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 领取宝箱奖励（/drawBox.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class DrawBox(
        var bizCode: Long = 0, // 业务返回码：1成功，0失败，-1无效的位置值，-2无效的卡片Id，-3当前位置没有宝箱，-4宝箱未打开，-5不是宝箱里的卡片，-6口袋空位不足
        var bizMessage: String? = null, // 错误提示信息
        var pocketCards: PocketCards? = null // 固定口袋卡片信息（刷新列表））
) : ProguardRule