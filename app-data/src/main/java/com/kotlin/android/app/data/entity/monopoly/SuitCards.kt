package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 套装卡片列表（/suitCardList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class SuitCards(
        var cardList: List<Card>? = null, // 卡片列表
        var suitInfo: Suit? = null, // 套装信息
        var suitType: Long = 1 // 套装类别 3为限量
) : ProguardRule