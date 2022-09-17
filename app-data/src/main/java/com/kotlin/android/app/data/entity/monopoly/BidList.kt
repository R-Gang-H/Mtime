package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 竞价列表（/bidList.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class BidList(
        var hasMore: Boolean = false,
        var totalCount: Long = 0,
        var bidList: List<Bid>? = null // 竞价列表
) : ProguardRule