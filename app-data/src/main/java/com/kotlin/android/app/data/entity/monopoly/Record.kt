package com.kotlin.android.app.data.entity.monopoly

/**
 * 游戏记录信息
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class Record(
        var gold: Long? = 0,
        var toolCards: List<PropCard>? = null,
        var senderInfo: UserInfo? = null,
        //信息状态：0接收者未处理，1接收者已处理
        var recordStatus: Long = 0,
        //信息类型：0赠送卡片 1使用道具，2发起交易，3议价，4交易成功，5交易失败，6拒绝交易，7拒绝议价，8邀请好友，
        // 9竞拍成功，10竞拍失败，11拍卖成功，12拍卖失败，13添加卡友，14添加卡友成功，15拒绝添加卡友，16删除卡友
        var recordType: Long = 0,
        var recordDetailId: Long = 0,
        var message: String? = null,
        var enterTime: Long = 0,
        var addPrice: Long = 0,
        var auctionGold: Long = 0,
        var auctionFee: Long = 0,
        //道具卡使用结果：1使用成功，2抢劫卡被防盗卡抵御/奴隶卡被反弹/黑客卡被反弹，3抢劫卡被闪避
        var toolCardResult: Long = 0L,
        var cards: List<Card>? = null,
        var suits: List<Suit>? = null
)