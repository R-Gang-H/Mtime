package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 保险箱空位
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class StrongBoxPosition(
        var strongBoxCount: Long = 0, // 保险箱数量
        var unlockStrongBoxCount: Long = 0, // 已解锁保险箱数量
        var position: Long = 0, // 空位序号（1-10）
        var isActive: Boolean = false, // 是否开通
        var cardList: List<Card>? = null // 卡片列表
) : ProguardRule