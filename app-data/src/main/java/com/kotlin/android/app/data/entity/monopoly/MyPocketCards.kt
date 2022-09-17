package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 我的口袋卡片列表（/myPocketCards.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class MyPocketCards(
        var cardList: ArrayList<Card>? = null, // 卡片列表
        var hitToolCard: HitToolCard? = null, // 命中道具卡信息
) : ProguardRule