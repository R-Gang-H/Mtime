package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.monopoly.*
import retrofit2.http.*

/**
 * 卡片大富翁模块接口定义：
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
interface ApiMTimeMonopoly {

    companion object {
        const val FRIEND_POCKET = "/richman/friendPocket.api" // 卡片大富翁api - TA的主页（/friendPocket.api）
        const val FRIEND_WISH = "/richman/friendWish.api" // 卡片大富翁api - TA的愿望（/friendWish.api）
        const val WISH_WALL = "/richman/wishWall.api" // 卡片大富翁api - 许愿墙（/wishWall.api.api）
        const val PICK_CARD_FROM_BID = "/richman/bidSuccessPickCard.api" // 卡片大富翁api - 一口价/竞价成功领取卡片（/bidSuccessPickCard.api）
        const val DISCARD = "/richman/batchDiscardToFriend.api" // 卡片大富翁api - 批量丢弃卡片给好友/机器人（/batchDiscardToFriend.api）
        const val TRADE_PRICE = "/richman/tradeRaisePrice.api" // 卡片大富翁api - 交易加价（/tradeRaisePrice.api）
        const val PICK_CARD_FROM_FRIEND = "/richman/pickCardFromFriend.api" // 卡片大富翁api - 从好友/机器人的开放口袋拾取卡片（/pickCardFromFriend.api）
        const val PICK_CARD_FROM_ME = "/richman/pickCardFromMe.api" // 卡片大富翁api - 从我的开放口袋拾取卡片（/pickCardFromMe.api）
        const val BATCH_PICK_CARD_FROM_FRIEND = "/richman/batchPickCardFromFriend.api" // 卡片大富翁api - 批量从好友/机器人的开放口袋拾取卡片（/batchPickCardFromFriend.api）
        const val BATCH_PICK_CARD_FROM_ME = "/richman/batchPickCardFromMe.api" // 卡片大富翁api - 批量从我的开放口袋拾取卡片（/batchPickCardFromMe.api）
        const val PICK_CARD_FROM_WISH = "/richman/pickCardFromWish.api" // 卡片大富翁api - 从许愿树取回卡片（/pickCardFromWish.api）
        const val USE_PROP_CARD = "/richman/useToolCard.api" // 卡片大富翁api - 使用道具卡（/useToolCard.api）
        const val ALL_SUIT_LIST = "/richman/allSuitList.api" // 卡片大富翁api - 全部套装列表（/allSuitList.api）
        const val DELETE_FRIEND = "/richman/deleteFriend.api" // 卡片大富翁api - 删除卡友（/deleteFriend.api）
        const val UPGRADE_SUIT = "/richman/upgradeSuit.api" // 卡片大富翁api - 升级套装（/upgradeSuit.api）
        const val START_TRADE = "/richman/startTrade.api" // 卡片大富翁api - 发起交易（/startTrade.api）
        const val BID = "/richman/bid.api" // 卡片大富翁api - 发起竞价（/bid.api）
        const val CANCEL_AUCTION = "/richman/cancelAuction.api" // 卡片大富翁api - 取消拍卖（/cancelAuction.api）
        const val MIX_SUIT = "/richman/mixSuit.api" // 卡片大富翁api - 合成套装（/mixSuit.api）
        const val AGREE_TRADE = "/richman/agreeTrade.api" // 卡片大富翁api - 同意交易（/agreeTrade.api）
        const val AGREE_FRIEND = "/richman/agreeFriend.api" // 卡片大富翁api - 同意添加卡友（/agreeFriend.api）
        const val BOX_LIST = "/richman/cardBoxList.api" // 卡片大富翁api - 商店宝箱列表（/cardBoxList.api）
        const val PROP_CARD_LIST = "/richman/toolCardList.api" // 卡片大富翁api - 商店道具卡列表（/toolCardList.api）
        const val SUIT_CARDS = "/richman/suitCardList.api" // 卡片大富翁api - 套装卡片列表（/suitCardList.api）
        const val SUIT_DETAIL = "/richman/suitDetail.api" // 卡片大富翁api - 套装详情弹层（/suitDetail.api）
        const val FRIEND_NORMAL_CARDS = "/richman/friendNormalCards.api" // 卡片大富翁api - 好友口袋普卡列表（/friendNormalCards.api）
        const val FRIEND_LIMIT_CARDS = "/richman/friendLimitCards.api" // 卡片大富翁api - 好友口袋限量   卡列表（/friendLimitCards.api）
        const val WISH_COME_TRUE = "/richman/wishComeTrue.api" // 卡片大富翁api - 帮TA实现愿望（/wishComeTrue.api）
        const val ACTIVE_STRONG_BOX_POSITION = "/richman/activeStrongBoxPosition.api" // 卡片大富翁api - 开通保险箱空位（/activeStrongBoxPosition.api）
        const val SUIT_LIST = "/richman/suitList.api" // 卡片大富翁api - 我/TA的套装（/suitList.api）
        const val MY_POCKET = "/richman/myPocket.api" // 卡片大富翁api - 我的主页（/myPocket.api）
        const val FRIENDS = "/richman/friendList.api" // 卡片大富翁api - 我的卡友（/friendList.api）
        const val MY_POCKET_CARDS = "/richman/myPocketCards.api" // 卡片大富翁api - 我的口袋卡片列表（/myPocketCards.api）
        const val MY_WISH = "/richman/myWish.api" // 卡片大富翁api - 我的愿望（/myWish.api）
        const val MY_PROP_CARDS = "/richman/myToolCard.api" // 卡片大富翁api - 我的道具卡（/myToolCard.api）
        const val OPEN_BOX = "/richman/openBox.api" // 卡片大富翁api - 打开宝箱（/openBox.api）
        const val DELETE_RECORDS = "/richman/deleteGameRecords.api" // 卡片大富翁api - 批量删除游戏信息（/deleteGameRecords.api）
        const val AUCTION_LIST = "/richman/auctionList.api" // 卡片大富翁api - 拍卖列表（/auctionList.api）
        const val REFUSE_TRADE = "/richman/refuseTrade.api" // 卡片大富翁api - 拒绝交易（/refuseTrade.api）
        const val REFUSE_FRIEND = "/richman/refuseFriend.api" // 卡片大富翁api - 拒绝添加卡友（/refuseFriend.api）
        const val LIMIT_SUIT_USERS = "/richman/limitSuitUsers.api" // 卡片大富翁api - 拥有限量版套装的会员列表（/limitSuitUsers.api）
        const val QUERY_SUIT_BY_NAME = "/richman/querySuitByName.api" // 卡片大富翁api - 按名称模糊搜索套装（/querySuitByName.api）
        const val DIG_BOX = "/richman/digBox.api" // 卡片大富翁api - 挖宝箱（/digBox.api）
        const val RANK_LIST = "/richman/topUserList.api" // 卡片大富翁api - 排行榜（/topUserList.api）
        const val HAS_DEMON_CARD = "/richman/hasDemonCard.api" // 卡片大富翁api - 是否有恶魔卡（/hasDemonCard.api）
        const val UPDATE_SIGNATURE = "/richman/updateSignature.api" // 卡片大富翁api - 更新签名档（/updateSignature.api）
        const val SIGNATURE_LIST = "/richman/signatureList.api" // 卡片大富翁api - 最新的签名档列表（/signatureList.api）
        const val ROBOT_POCKET = "/richman/robotPocket.api" // 卡片大富翁api - 机器人的主页（/robotPocket.api）
        const val MY_CARD_BY_SUIT = "/richman/myCardBySuit.api" // 卡片大富翁api - 某套装下我的卡片信息（/myCardBySuit.api）
        const val PICK_CARD_FROM_AUCTION = "/richman/auctionFailedPickCard.api" // 卡片大富翁api - 流拍取回卡片（/auctionFailedPickCard.api）
        const val ADD_FRIEND = "/richman/addFriend.api" // 卡片大富翁api - 添加卡友（/addFriend.api）
        const val ADD_OR_CHANGE_WISH = "/richman/addOrChangeWish.api" // 卡片大富翁api - 添加或更换许愿（/addOrChangeWish.api）
        const val ADD_AUCTION = "/richman/addAuction.api" // 卡片大富翁api - 添加拍卖（/addAuction.api）
        const val RECORD_LIST = "/richman/gameRecord.api" // 卡片大富翁api - 游戏信息列表（/gameRecord.api）
        const val MOVE_CARD_TO_STRONG_BOX = "/richman/batchMoveCardToStrongBox.api" // 卡片大富翁api - 批量移动卡片到保险箱（/batchMoveCardToStrongBox.api）
        const val MOVE_CARD_TO_POCKET =     "/richman/batchMoveCardToPocket.api" // 卡片大富翁api - 批量移动卡片到固定口袋（/batchMoveCardToPocket.api）
        const val BID_LIST =                "/richman/bidList.api" // 卡片大富翁api - 竞价列表（/bidList.api）
        const val UNLOCK_STRONG_BOX =       "/richman/unlockStrongBox.api" // 卡片大富翁api - 解锁保险箱（/unlockStrongBox.api）
        const val BUY_LIST =                "/richman/buyList.api" // 卡片大富翁api - 购买列表（/buyList.api）
        const val BUY_CARD_BOX =            "/richman/buyCardBox.api" // 卡片大富翁api - 购买宝箱（/buyCardBox.api）
        const val DRAW_BOX =                "/richman/drawBox.api" // 卡片大富翁api - 领取宝箱奖励（/drawBox.api）
        const val BUY_TOOL_CARD =           "/richman/buyToolCard.api" // 卡片大富翁api - 购买道具卡（/buyToolCard.api）
        const val BUY_BATCH_TOOL_CARD =     "/richman/batchBuyToolCard.api" // 卡片大富翁api - 批量购买道具卡（/buyToolCard.api）
        const val USER_DETAIL =             "/richman/userDetail.api" //卡片大富翁api - 主页用户详情弹层数据（/userDetail.api）
        const val CURRENT_ISSUE_SUIT_LIST = "/richman/currentIssueSuitList.api" // 片大富翁api - 获取正在发行的套装列表（/currentIssueSuitList.api）
        const val SUIT_SHOW =               "/richman/setSuitShow.api" // 卡片大富翁api - 设置主页展示套装（/setSuitShow.api）
        const val SEARCH_SUIT_LIST =        "/richman/searchSuitList.api" // 卡片大富翁api - 我的套装搜索结果列表（/searchSuitList.api）
    }

    /**
     * 卡片大富翁api - TA的主页（/friendPocket.api）
     * POST ("/richman/friendPocket.api")
     *
     * friendId Number  好友Id
     */
    @POST(FRIEND_POCKET)
    @FormUrlEncoded
    suspend fun postFriendPocket(
            @Field("friendId") friendId: Long
    ): ApiResponse<FriendPocket>

    /**
     * 卡片大富翁api - TA的愿望（/friendWish.api）
     * POST ("/richman/friendWish.api")
     *
     * friendId Number  好友Id
     */
    @POST(FRIEND_WISH)
    @FormUrlEncoded
    suspend fun postFriendWish(
            @Field("friendId") friendId: Long
    ): ApiResponse<FriendWish>

    /**
     * 卡片大富翁api - TA的愿望（/friendWish.api）
     * POST ("/richman/friendWish.api")
     *
     * friendId Number  好友Id
     */
    @POST(WISH_WALL)
    @FormUrlEncoded
    suspend fun getWishWall(
        @Field("pageIndex") pageIndex: Long,
        @Field("pageSize") pageSize: Long
    ): ApiResponse<WishWall>

    /**
     * 卡片大富翁api - 一口价/竞价成功领取卡片（/bidSuccessPickCard.api）
     * POST ("/richman/bidSuccessPickCard.api")
     *
     * auctionId	Number  拍卖Id
     */
    @POST(PICK_CARD_FROM_BID)
    @FormUrlEncoded
    suspend fun postPickCardFromBid(
            @Field("auctionId") auctionId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 批量丢弃卡片给好友/机器人（/batchDiscardToFriend.api）
     * POST ("/richman/batchDiscardToFriend.api")
     *
     * userCardIds	Number  逗号分隔的多个用户卡片Id
     * friendId	    Number  好友Id
     * isRobot	    Boolean 好友是否机器人
     */
    @POST(DISCARD)
    @FormUrlEncoded
    suspend fun postDiscard(
            @Field("userCardIds") userCardIds: String,
            @Field("friendId") friendId: Long,
            @Field("isRobot") isRobot: Boolean
    ): ApiResponse<Discard>

    /**
     * 卡片大富翁api - 交易加价（/tradeRaisePrice.api）
     * POST ("/richman/tradeRaisePrice.api")
     *
     * recordDetailId	Number  信息详情Id
     * raiseGolds	    Number  加价金币数
     * message	        String  附言
     */
    @POST(TRADE_PRICE)
    @FormUrlEncoded
    suspend fun postTradePrice(
            @Field("recordDetailId") recordDetailId: Long,
            @Field("raiseGolds") raiseGolds: Long,
            @Field("message") message: String,
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 从好友/机器人的开放口袋拾取卡片（/pickCardFromFriend.api）
     * POST ("/richman/pickCardFromFriend.api")
     *
     * userCardId	Number  要拾取的用户卡片Id
     * friendId	    Number  好友Id
     * isRobot	    Boolean 好友是否机器人
     */
    @POST(PICK_CARD_FROM_FRIEND)
    @FormUrlEncoded
    suspend fun postPickCardFromFriend(
            @Field("userCardId") userCardId: Long,
            @Field("friendId") friendId: Long,
            @Field("isRobot") isRobot: Boolean
    ): ApiResponse<PickCard>

    /**
     * 卡片大富翁api - 从我的开放口袋拾取卡片（/pickCardFromMe.api）
     * POST ("richman/pickCardFromMe.api")
     *
     * userCardId	Number  要拾取的用户卡片Id
     */
    @POST(PICK_CARD_FROM_ME)
    @FormUrlEncoded
    suspend fun postPickCardFromMe(
            @Field("userCardId") userCardId: String
    ): ApiResponse<PickCardFromMe>

    /**
     * 卡片大富翁api - 批量从好友/机器人的开放口袋拾取卡片（/batchPickCardFromFriend.api）
     * POST ("/richman/batchPickCardFromFriend.api")
     *
     * userCardIds	String  逗号分隔的多个要拾取的用户卡片Id
     * friendId	    Number  好友Id
     * isRobot	    Boolean 好友是否机器人
     */
    @POST(BATCH_PICK_CARD_FROM_FRIEND)
    @FormUrlEncoded
    suspend fun postBatchPickCardFromFriend(
            @Field("userCardIds") userCardIds: String,
            @Field("friendId") friendId: Long,
            @Field("isRobot") isRobot: Boolean
    ): ApiResponse<PickCard>

    /**
     * 卡片大富翁api - 批量从我的开放口袋拾取卡片（/batchPickCardFromMe.api）
     * POST ("richman/batchPickCardFromMe.api")
     *
     * userCardIds	String  逗号分隔的多个要拾取的用户卡片Id
     */
    @POST(BATCH_PICK_CARD_FROM_ME)
    @FormUrlEncoded
    suspend fun postBatchPickCardFromMe(
            @Field("userCardIds") userCardIds: String
    ): ApiResponse<PickCardFromMe>

    /**
     * 卡片大富翁api - 从许愿树取回卡片（/pickCardFromWish.api）
     * POST ("/richman/pickCardFromWish.api")
     */
    @POST(PICK_CARD_FROM_WISH)
    suspend fun postPickCardFromWish(): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 使用道具卡（/useToolCard.api）
     * POST ("/richman/useToolCard.api")
     *
     * cardToolId	Number  要使用的道具卡Id
     * targetUserId	Number  目标用户Id（对谁使用）
     * targetToolId	Number  目标道具卡Id（使用复制卡时必传此参数）
     * targetCardId	Number  目标卡片Id（使用打劫卡时必传此参数）
     * useDemonCard	Boolean 是否使用恶魔卡（使用奴隶卡或黑客卡时必传此参数）
     */
    @POST(USE_PROP_CARD)
    @FormUrlEncoded
    suspend fun postUsePropCard(
            @Field("cardToolId") cardToolId: Long,
            @Field("targetUserId") targetUserId: Long,
            @Field("targetToolId") targetToolId: Long? = null,
            @Field("targetCardId") targetCardId: Long? = null,
            @Field("useDemonCard") useDemonCard: Boolean? = null
    ): ApiResponse<UseToolResult>

    /**
     * 卡片大富翁api - 全部套装列表（/allSuitList.api）
     * POST ("/richman/allSuitList.api")
     *
     * suitType	    Number  1普通套装，2限量套装
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数
     */
    @POST(ALL_SUIT_LIST)
    @FormUrlEncoded
    suspend fun postAllSuitList(
            @Field("suitType") suitType: Long,
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long
    ): ApiResponse<SuitList>

    /**
     * 卡片大富翁api - 删除卡友（/deleteFriend.api）
     * POST ("/richman/deleteFriend.api")
     *
     * friendId	Number  好友Id
     */
    @POST(DELETE_FRIEND)
    @FormUrlEncoded
    suspend fun postDeleteFriend(
            @Field("friendId") friendId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 升级套装（/upgradeSuit.api）
     * POST ("/richman/upgradeSuit.api")
     *
     * suitId	    Number  套装Id
     * suitClass	String  套装当前级别：c,b,a
     */
    @POST(UPGRADE_SUIT)
    @FormUrlEncoded
    suspend fun postUpgradeSuit(
            @Field("suitId") suitId: Long,
            @Field("suitClass") suitClass: String
    ): ApiResponse<UpgradeSuit>

    /**
     * 卡片大富翁api - 发起交易（/startTrade.api）
     * POST ("/richman/startTrade.api")
     *
     * friendId	    Number  好友Id
     * myCardId	    Number  我的卡片Id
     * friendCardId	Number  好友的卡片Id
     * gold	        Number  加价金币数（不填传0）
     * message	    String  附言
     */
    @POST(START_TRADE)
    @FormUrlEncoded
    suspend fun postStartTrade(
            @Field("friendId") friendId: Long,
            @Field("myCardId") myCardId: Long,
            @Field("friendCardId") friendCardId: Long,
            @Field("gold") gold: Long,
            @Field("message") message: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 发起竞价（/bid.api）
     * POST ("/richman/bid.api")
     *
     * auctionId	Number  竞价Id
     * bidPrice	    Number  竞拍价(金币数)
     */
    @POST(BID)
    @FormUrlEncoded
    suspend fun postBid(
            @Field("auctionId") auctionId: Long,
            @Field("bidPrice") bidPrice: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 取消拍卖（/cancelAuction.api）
     * POST ("/richman/cancelAuction.api")
     *
     * auctionId	Number  拍卖Id
     */
    @POST(CANCEL_AUCTION)
    @FormUrlEncoded
    suspend fun postCancelAuction(
            @Field("auctionId") auctionId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 合成套装（/mixSuit.api）
     * POST ("/richman/mixSuit.api")
     *
     * userCardIds	String  要合成的用户卡片Id列表（逗号分隔）
     */
    @POST(MIX_SUIT)
    @FormUrlEncoded
    suspend fun postMixSuit(
            @Field("userCardIds") userCardIds: String
    ): ApiResponse<MixSuit>

    /**
     * 卡片大富翁api - 同意交易（/agreeTrade.api）
     * POST ("/richman/agreeTrade.api")
     *
     * recordDetailId	Number  信息详情Id
     */
    @POST(AGREE_TRADE)
    @FormUrlEncoded
    suspend fun postAgreeTrade(
            @Field("recordDetailId") recordDetailId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 同意添加卡友（/agreeFriend.api）
     * POST ("/richman/agreeFriend.api")
     *
     * recordDetailId	Number  信息详情Id
     */
    @POST(AGREE_FRIEND)
    @FormUrlEncoded
    suspend fun postAgreeFriend(
            @Field("recordDetailId") recordDetailId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 商店宝箱列表（/cardBoxList.api）
     * POST ("/richman/cardBoxList.api")
     */
    @POST(BOX_LIST)
    suspend fun postBoxList(): ApiResponse<BoxList>

    /**
     * // 卡片大富翁api - 商店道具卡列表（/toolCardList.api）
     * POST ("/richman/toolCardList.api")
     */
    @POST(PROP_CARD_LIST)
    suspend fun postPropCardList(): ApiResponse<PropList>

    /**
     * 卡片大富翁api - 套装卡片列表（/suitCardList.api）
     * POST ("/richman/suitCardList.api")
     *
     * suitId	Number  套装Id
     */
    @POST(SUIT_CARDS)
    @FormUrlEncoded
    suspend fun postSuitCards(
            @Field("suitId") suitId: Long
    ): ApiResponse<SuitCards>

    /**
     * 卡片大富翁api - 套装详情弹层（/suitDetail.api）
     * POST ("/richman/suitDetail.api")
     *
     * suitId	    Number  套装Id
     * suitClass	Number  套装级别：c,b,a
     * suitUserId	Number  套装所属用户Id
     */
    @POST(SUIT_DETAIL)
    @FormUrlEncoded
    suspend fun postSuitDetail(
            @Field("suitId") suitId: Long,
            @Field("suitClass") suitClass: String,
            @Field("suitUserId") suitUserId: Long
    ): ApiResponse<SuitDetail>

    /**
     * 卡片大富翁api - 好友口袋普卡列表（/friendNormalCards.api）
     * POST ("/richman/friendNormalCards.api")
     *
     * friendId	Number  好友Id
     */
    @POST(FRIEND_NORMAL_CARDS)
    @FormUrlEncoded
    suspend fun postFriendNormalCards(
            @Field("friendId") friendId: Long
    ): ApiResponse<FriendNormalCards>

    /**
     * 卡片大富翁api - 好友口袋普卡列表（/friendNormalCards.api）
     * POST ("/richman/friendNormalCards.api")
     *
     * friendId	Number  好友Id
     */
    @POST(FRIEND_LIMIT_CARDS)
    @FormUrlEncoded
    suspend fun postFriendLimitCards(
            @Field("friendId") friendId: Long
    ): ApiResponse<FriendNormalCards>

    /**
     * 卡片大富翁api - 帮TA实现愿望（/wishComeTrue.api）
     * POST ("/richman/wishComeTrue.api")
     *
     * friendId	Number  好友Id
     */
    @POST(WISH_COME_TRUE)
    @FormUrlEncoded
    suspend fun postWishComeTrue(
            @Field("friendId") friendId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 开通保险箱空位（/activeStrongBoxPosition.api）
     * POST ("/richman/activeStrongBoxPosition.api")
     *
     * position	Number  要开通的空位序号（1-10）
     */
    @POST(ACTIVE_STRONG_BOX_POSITION)
    @FormUrlEncoded
    suspend fun postActiveStrongBoxPosition(
            @Field("position") position: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 我/TA的套装（/suitList.api）
     * POST ("/richman/suitList.api")
     *
     * userId	    Number  用户Id
     * categoryId	Number  套装列表分类Id：1简装版套装，2精装版套装，3终极版套装，4限量版套装
     * pageIndex	Number  当前页索引
     * pageSize 	Number  每页记录数
     */
    @POST(SUIT_LIST)
    @FormUrlEncoded
    suspend fun postSuitList(
            @Field("userId") userId: Long,
            @Field("categoryId") categoryId: Long,
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long
    ): ApiResponse<SuitList>

    /**
     * 卡片大富翁api - 我的主页（/myPocket.api）
     * POST ("/richman/myPocket.api")
     */
    @POST(MY_POCKET)
    suspend fun postMyPocket(): ApiResponse<MyPocket>

    /**
     * 卡片大富翁api - 我的卡友（/friendList.api）
     * POST ("/richman/friendList.api")
     *
     * orderType	Number  排序类型：1最多空位，2最多钱，3最多套装
     * pageIndex	Number  当前页索引
     * pageSize 	Number  每页记录数
     */
    @POST(FRIENDS)
    @FormUrlEncoded
    suspend fun postFriends(
            @Field("orderType") orderType: Long,
            @Field("friendName") friendName: String,
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long
    ): ApiResponse<Friends>

    /**
     * 卡片大富翁api - 我的口袋卡片列表（/myPocketCards.api）
     * POST ("/richman/myPocketCards.api")
     */
    @POST(MY_POCKET_CARDS)
    suspend fun postMyPocketCards(): ApiResponse<MyPocketCards>

    /**
     * 卡片大富翁api - 我的愿望（/myWish.api）
     * POST ("/richman/myWish.api")
     */
    @POST(MY_WISH)
    suspend fun postMyWish(): ApiResponse<MyWish>

    /**
     * 卡片大富翁api - 我的道具卡（/myToolCard.api）
     * POST ("/richman/myToolCard.api")
     */
    @POST(MY_PROP_CARDS)
    suspend fun postMyPropCards(): ApiResponse<MyPropCards>

    /**
     * 卡片大富翁api - 打开宝箱（/openBox.api）
     * POST ("/richman/openBox.api")
     *
     * position	Number  宝箱位置（1-4）
     */
    @POST(OPEN_BOX)
    @FormUrlEncoded
    suspend fun postOpenBox(
            @Field("position") position: Long,
            @Field("openWithGold") openWithGold: Boolean
    ): ApiResponse<OpenBox>

    /**
     * 卡片大富翁api - 批量删除游戏信息（/deleteGameRecords.api）
     * GET ("/richman/deleteGameRecords.api")
     *
     * recordDetailIds	Array   信息详情Ids（逗号分隔）
     */
    @POST(DELETE_RECORDS)
    suspend fun getDeleteRecords(
            @Query("recordDetailIds") recordDetailIds: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 拍卖列表（/auctionList.api）
     * POST ("/richman/auctionList.api")
     *
     * pageIndex	Number  当前页索引
     * pageSize 	Number  每页记录数
     */
    @POST(AUCTION_LIST)
    @FormUrlEncoded
    suspend fun postAuctionList(
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long
    ): ApiResponse<AuctionList>

    /**
     * 卡片大富翁api - 拒绝交易（/refuseTrade.api）
     * POST ("/richman/refuseTrade.api")
     *
     * recordDetailId	Number  信息详情Id
     * message	        String  附言
     */
    @POST(REFUSE_TRADE)
    @FormUrlEncoded
    suspend fun postRefuseTrade(
            @Field("recordDetailId") recordDetailId: Long,
            @Field("message") message: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 拒绝添加卡友（/refuseFriend.api）
     * POST ("/richman/refuseFriend.api")
     *
     * recordDetailId	Number  信息详情Id
     * message	        String  附言
     */
    @POST(REFUSE_FRIEND)
    @FormUrlEncoded
    suspend fun postRefuseFriend(
            @Field("recordDetailId") recordDetailId: Long,
            @Field("message") message: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 拥有限量版套装的会员列表（/limitSuitUsers.api）
     * POST ("/richman/limitSuitUsers.api")
     *
     * limitSuitId	Number  限量版套装Id
     */
    @POST(LIMIT_SUIT_USERS)
    @FormUrlEncoded
    suspend fun postLimitSuitUsers(
            @Field("limitSuitId") limitSuitId: Long
    ): ApiResponse<LimitSuitUsers>

    /**
     * 卡片大富翁api - 按名称模糊搜索套装（/querySuitByName.api）
     * POST ("/richman/querySuitByName.api")
     *
     * suitName	String  套装名称关键字
     * getCount	Number  获取数量（默认10）
     */
    @POST(QUERY_SUIT_BY_NAME)
    @FormUrlEncoded
    suspend fun postQuerySuitByCard(
            @Field("suitName") suitName: String,
            @Field("getCount") getCount: Long
    ): ApiResponse<QuerySuitList>

    /**
     * 卡片大富翁api - 挖宝箱（/digBox.api）
     * POST ("/richman/digBox.api")
     */
    @POST(DIG_BOX)
    suspend fun postDigBox(): ApiResponse<DigBox>

    /**
     * 卡片大富翁api - 排行榜（/topUserList.api）
     * POST ("/richman/topUserList.api")
     *
     * rankType	Number  排行榜分类：1昨日道具狂人，2昨日衰人，3昨日交易达人，4昨日收藏大玩家，5金币大富翁，6套装组合狂
     */
    @POST(RANK_LIST)
    @FormUrlEncoded
    suspend fun postRankList(
            @Field("rankType") rankType: Long
    ): ApiResponse<Rank>

    /**
     * 卡片大富翁api - 是否有恶魔卡（/hasDemonCard.api）
     * POST ("/richman/hasDemonCard.api")
     */
    @POST(HAS_DEMON_CARD)
    suspend fun postHasDemonCard(): ApiResponse<BooleanResult>

    /**
     * 卡片大富翁api - 更新签名档（/updateSignature.api）
     * POST ("/richman/updateSignature.api")
     *
     * signature	String  签名档内容
     */
    @POST(UPDATE_SIGNATURE)
    @FormUrlEncoded
    suspend fun postUpdateSignature(
            @Field("signature") signature: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 最新的签名档列表（/signatureList.api）
     * POST ("/richman/signatureList.api")
     *
     * getCount	Number  获取数量（默认20）
     */
    @POST(SIGNATURE_LIST)
    @FormUrlEncoded
    suspend fun postSignatureList(
            @Field("getCount") getCount: Long
    ): ApiResponse<SignatureList>

    /**
     * 卡片大富翁api - 机器人的主页（/robotPocket.api）
     * POST ("/richman/robotPocket.api")
     *
     * robotId	Number  机器人Id
     */
    @POST(ROBOT_POCKET)
    @FormUrlEncoded
    suspend fun postRobotPocket(
            @Field("robotId") robotId: Long
    ): ApiResponse<Robot>

    /**
     * 卡片大富翁api - 某套装下我的卡片信息（/myCardBySuit.api）
     * POST ("/richman/myCardBySuit.api")
     *
     * suitId	Number  套装Id
     */
    @POST(MY_CARD_BY_SUIT)
    @FormUrlEncoded
    suspend fun postMyCardBySuit(
            @Field("suitId") suitId: Long
    ): ApiResponse<MyCardsBySuit>

    /**
     * 卡片大富翁api - 流拍取回卡片（/auctionFailedPickCard.api）
     * POST ("/richman/auctionFailedPickCard.api")
     *
     * auctionId	Number  拍卖Id
     */
    @POST(PICK_CARD_FROM_AUCTION)
    @FormUrlEncoded
    suspend fun postPickCardFromAuction(
            @Field("auctionId") auctionId: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 添加卡友（/addFriend.api）
     * POST ("/richman/addFriend.api")
     *
     * friendId	Number  好友Id
     * message	String  附言
     */
    @POST(ADD_FRIEND)
    @FormUrlEncoded
    suspend fun postAddFriend(
            @Field("friendId") friendId: Long,
            @Field("message") message: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 添加或更换许愿（/addOrChangeWish.api）
     * POST ("/richman/addOrChangeWish.api")
     *
     * cardId	Number  许愿卡片Id
     * content	String  许愿内容
     */
    @POST(ADD_OR_CHANGE_WISH)
    @FormUrlEncoded
    suspend fun postAddOrChangeWish(
            @Field("cardId") cardId: Long,
            @Field("content") content: String
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 添加拍卖（/addAuction.api）
     * POST ("/richman/addAuction.api")
     *
     * id	        Number  卡片Id
     * type      	Number  1-卡片,2-道具卡,3-套装
     * timeLimited	Number  拍卖时限：1 2小时，2 8小时，3 16小时
     * startPrice	Number  底价
     * fixPrice 	Number  一口价
     */
    @POST(ADD_AUCTION)
    @FormUrlEncoded
    suspend fun postAddAuction(
            @Field("id") id: Long? = null,
            @Field("type") type: Long? = null,
            @Field("timeLimited") timeLimited: Long,
            @Field("startPrice") startPrice: Long,
            @Field("fixPrice") fixPrice: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 游戏信息列表（/gameRecord.api）
     * POST ("/richman/gameRecord.api")
     *
     * listType 	Number  列表分类：1交易信息，2道具卡信息，3卡友信息
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数
     */
    @POST(RECORD_LIST)
    @FormUrlEncoded
    suspend fun postRecordList(
            @Field("listType") listType: Long,
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long,
            @Field("type") type: Long,
    ): ApiResponse<RecordList>

    /**
     * 卡片大富翁api - 批量移动卡片到保险箱（/batchMoveCardToStrongBox.api）
     * POST ("/richman/batchMoveCardToStrongBox.api")
     *
     * userCardIds	String  逗号分隔的多个用户卡片Id
     */
    @POST(MOVE_CARD_TO_STRONG_BOX)
    @FormUrlEncoded
    suspend fun postMoveCardToStrongBox(
            @Field("userCardIds") userCardIds: String
    ): ApiResponse<MoveCard>

    /**
     * 卡片大富翁api - 批量移动卡片到固定口袋（/batchMoveCardToPocket.api）
     * POST ("/richman/batchMoveCardToPocket.api")
     *
     * userCardIds	String  逗号分隔的多个用户卡片Id
     */
    @POST(MOVE_CARD_TO_POCKET)
    @FormUrlEncoded
    suspend fun postMoveCardToPocket(
            @Field("userCardIds") userCardIds: String
    ): ApiResponse<MoveCard>

    /**
     * 卡片大富翁api - 竞价列表（/bidList.api）
     * POST ("/richman/bidList.api")
     *
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数
     */
    @POST(BID_LIST)
    @FormUrlEncoded
    suspend fun postBidList(
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long
    ): ApiResponse<BidList>

    /**
     * 卡片大富翁api - 解锁保险箱（/unlockStrongBox.api）
     * POST ("/richman/unlockStrongBox.api")
     *
     * position	Number  要解锁的空位序号（1-10）
     */
    @POST(UNLOCK_STRONG_BOX)
    @FormUrlEncoded
    suspend fun postUnlockStrongBox(
            @Field("position") position: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 购买列表（/buyList.api）
     * POST ("/richman/buyList.api")
     *
     * orderType	Number  排序类型：1结束时间倒序，2结束时间正序，3一口价倒序，4一口价正序，5当前价倒序，6当前价正序
     * isToolCard	Number  是否搜索道具卡
     * cardId	    Number  指定的卡片Id
     * pageIndex	Number  当前页索引
     * pageSize	    Number  每页记录数
     */
    @POST(BUY_LIST)
    @FormUrlEncoded
    suspend fun postBuyList(
            @Field("orderType") orderType: Long,
            @Field("queryType") queryType: Long,
            @Field("queryId") queryId: Long,
            @Field("pageIndex") pageIndex: Long,
            @Field("pageSize") pageSize: Long
    ): ApiResponse<BuyList>

    /**
     * 卡片大富翁api - 购买宝箱（/buyCardBox.api）
     * GET ("/richman/buyCardBox.api")
     *
     * cardBoxId	Number  宝箱Id
     */
    @POST(BUY_CARD_BOX)
    suspend fun getBuyCardBox(
            @Query("cardBoxId") cardBoxId: Long
    ): ApiResponse<OpenBox>

    /**
     * 卡片大富翁api - 领取宝箱奖励（/drawBox.api）
     * POST ("/richman/drawBox.api")
     *
     * position	Number  宝箱位置（1-4，商店购买的宝箱传0）
     * cardIds	Array   要领取的卡片Ids（逗号分隔，不传认为全部领取）
     */
    @POST(DRAW_BOX)
    @FormUrlEncoded
    suspend fun postDrawBox(
            @Field("position") position: Long,
            @Field("cardIds") cardIds: String
    ): ApiResponse<DrawBox>

    /**
     * 购买道具卡
     */
    @POST(BUY_TOOL_CARD)
    @FormUrlEncoded
    suspend fun buyToolsCard(
            @Field("toolCardId") toolCardId: Long
    ): ApiResponse<CommResult>

    /**
     * 批量购买道具卡
     */
    @POST(BUY_BATCH_TOOL_CARD)
    @FormUrlEncoded
    suspend fun buyBatchToolsCard(
            @Field("toolCardId") toolCardId: Long,
            @Field("num") num: Long
    ): ApiResponse<CommResult>

    /**
     * 卡片大富翁api - 主页用户详情弹层数据（/userDetail.api
     * GET ("/richman/userDetail.api")
     *
     * userId	Number
     */
    @GET(USER_DETAIL)
    suspend fun getUserDetail(@Query("userId") userId: Long): ApiResponse<UserDetail>

    /**
     * 片大富翁api - 获取正在发行的套装列表（/currentIssueSuitList.api）
     * GET ("/richman/currentIssueSuitList.api")
     */
    @GET(CURRENT_ISSUE_SUIT_LIST)
    suspend fun getCurrentIssueSuitList(): ApiResponse<CurrentIssueSuitList>

    /**
     * 卡片大富翁api - 设置主页展示套装（/setSuitShow.api）
     * POST ("/richman/setSuitShow.api")
     *
     * cardUserSuitId	Number  要修改的用户套装Id（等于0表示新增，大于0表示修改）
     * suitId	        Number  要替换成的套装Id（等于0表示取消当前设置）
     */
    @POST(SUIT_SHOW)
    @FormUrlEncoded
    suspend fun postSuitShow(
        @Field("cardUserSuitId") cardUserSuitId: Long,
        @Field("suitId") suitId: Long
    ): ApiResponse<SuitShow>

    /**
     * 卡片大富翁api - 我的套装搜索结果列表（/searchSuitList.api）
     * POST ("/richman/searchSuitList.api")
     *
     * userId	Number  用户Id
     * suitId	Number  套装Id
     */
    @POST(SEARCH_SUIT_LIST)
    @FormUrlEncoded
    suspend fun postSearchSuitList(
        @Field("userId") userId: Long,
        @Field("suitId") suitId: Long
    ): ApiResponse<SearchSuitList>

}