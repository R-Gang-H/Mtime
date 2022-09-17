package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 某套装下我的卡片信息（/myCardBySuit.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class MyCardsBySuit(
        var selectSuitInfo: Suit? = null,
        var lackCards: List<LackCard>? = null,
        var ownedCards: List<Card>? = null
) : ProguardRule