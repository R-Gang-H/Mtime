package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 我的道具卡（/myToolCard.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class MyPropCards(
        var userInfo: UserInfo? = null, // 当前用户信息
        var toolCardList: List<PropCard>? = null // 道具卡列表
) : ProguardRule