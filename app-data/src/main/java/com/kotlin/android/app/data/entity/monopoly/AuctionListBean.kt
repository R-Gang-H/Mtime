package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * @desc 拍卖卡列表
 * @author zhangjian
 * @date 2020/9/18 15:13
 */

data class AuctionListBean(
    var auctionList: List<Auction> = listOf(),
    var hasMore: Boolean = false, // false
    var totalCount: Long = 0 // 3334
): ProguardRule {
    data class Auction(
        var auctionCard: AuctionCard = AuctionCard(),
        var auctionEndTime: Long = 0, // 3398
        var auctionId: Long = 0, // 6018
        var auctionStatus: Long = 0, // 7124
        var bidPrice: Long = 0, // 9263
        var bidUserId: Long = 0, // 1506
        var destroyTime: Long = 0, // 5486
        var fixPrice: Long = 0, // 7550
        var startPrice: Long = 0, // 6536
        var vendorInfo: VendorInfo = VendorInfo()
    ): ProguardRule {
        data class AuctionCard(
            var cardCover: String = "", // U32NgKX
            var cardId: Long = 0, // 1157
            var cardSuitId: Long = 0 // 7698
        ): ProguardRule

        data class VendorInfo(
            var avatarUrl: String = "", // JYm0lFXu
            var friendId: Long = 0, // 2838
            var isOnline: Boolean = false, // true
            var nickName: String = "" // Is3I
        ): ProguardRule
    }
}