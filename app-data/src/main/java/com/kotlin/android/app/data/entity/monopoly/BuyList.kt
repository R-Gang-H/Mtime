package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 购买列表（/buyList.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class BuyList(
        var hasMore: Boolean = false,
        var totalCount: Long = 0,
        var buyList: List<Bid>? = null // 购买列表
) : ProguardRule