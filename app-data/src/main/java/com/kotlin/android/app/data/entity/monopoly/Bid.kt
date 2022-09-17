package com.kotlin.android.app.data.entity.monopoly

/**
 * 竞价
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class Bid(
        var auctionId: Long, // 拍卖Id
        var auctionCard: Card, // 拍卖卡片/工具卡信息
        var auctionStatus: Long, // 拍卖状态：0拍卖进行中，1一口价成交待领取，2流拍待取回，3一口价/竞拍成功已领取，4流拍已取回，6取消拍卖已取回
        var auctionEndTime: Long, // 拍卖结束时间（时间戳，单位秒）
        var destroyTime: Long, // 卡片销毁时间（时间戳，单位秒）
        var startPrice: Long, // 底价（金币数）
        var fixPrice: Long, // 一口价（金币数）
        var bidPrice: Long, // 竞拍价（金币数）
        var bidUserId: Long, // 竞拍人Id
        var vendorInfo: UserInfo // 卖家信息
)