package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 商店道具卡列表（/toolCardList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class PropList(
        var toolCardList: List<PropCard>? = null // 道具卡列表
) : ProguardRule