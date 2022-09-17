package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 拍卖信息
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class Auction(
        var auctionId: Long = 0, // 拍卖Id
        var auctionCard: Card? = null, // 拍卖卡片/工具卡信息
        var auctionStatus: Long = 0, // 拍卖状态：0拍卖进行中，1一口价成交待领取，2流拍待取回，3一口价/竞拍成功已领取，4流拍已取回，6取消拍卖已取回
        var auctionEndTime: Long = 0, // 拍卖结束时间（时间戳，单位秒）
        var destroyTime: Long = 0, // 卡片销毁时间（时间戳，单位秒）
        var startPrice: Long = 0, // 底价（金币数）
        var fixPrice: Long = 0, // 一口价（金币数）
        var bidPrice: Long = 0, // 竞拍价（金币数）
        var bidUserNickName: String? = null,//竞拍人昵称
        var bidUserId: Long = 0, // 竞拍人Id
        var vendorInfo: UserInfo? = null // 卖家信息
) : ProguardRule