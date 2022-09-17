package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 我的愿望信息
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class MyWish(
        var wishInfo: WishInfo? = null, // 许愿信息
        var selectSuitInfo: Suit? = null, // 当前选中的套装信息
        var suitList: List<Suit>? = null, // 我的卡片所属套装列表
        var lackCards: List<LackCard>? = null, // 我缺少的卡片列表
        var ownedCards: List<Card>? = null // 我拥有的卡片列表
) : ProguardRule