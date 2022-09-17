package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 拍卖列表（/auctionList.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class AuctionList(
        var hasMore: Boolean = false, // 是否有更多
        var totalCount: Long = 0, // 总记录数
        var auctionList: List<Auction>? = null // 拍卖列表
) : ProguardRule