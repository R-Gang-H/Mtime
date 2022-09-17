package com.kotlin.android.card.monopoly.repository

import androidx.annotation.IntRange
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.monopoly.*

/**
 *
 * Created on 2020/6/18.
 *
 * @author o.s
 */
class CardMonopolyApiRepository : BaseRepository() {

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    suspend fun getShareInfo(
        type: Long = 10L,
        relateId: Long? = null,
        secondRelateId: Long? = null
    ): ApiResult<CommonShare> {
        return request {
            apiMTime.getShareInfo(type, relateId, secondRelateId)
        }
    }

    /**
     * TA的主页
     */
    suspend fun friendPocket(friendId: Long): ApiResult<FriendPocket> {
        return request {
            apiMTime.postFriendPocket(friendId)

        }
    }

    /**
     * 我的主页
     */
    suspend fun myPocket(): ApiResult<MyPocket> {
        return request {
            apiMTime.postMyPocket()
        }
    }

    /**
     * 我的口袋卡片列表
     */
    suspend fun myPocketCards(): ApiResult<MyPocketCards> {
        return request {
            apiMTime.postMyPocketCards()
        }
    }

    /**
     * 我的口袋卡片列表
     */
    suspend fun friendNormalCards(friendId: Long): ApiResult<FriendNormalCards> {
        return request {
            apiMTime.postFriendNormalCards(friendId)
        }
    }

    /**
     * 我的口袋限量卡片列表
     */
    suspend fun friendLimitCards(friendId: Long): ApiResult<FriendNormalCards> {
        return request {
            apiMTime.postFriendLimitCards(friendId)
        }
    }

    /**
     * 我的道具卡
     */
    suspend fun myPropCards(): ApiResult<MyPropCards> {
        return request {
            apiMTime.postMyPropCards()
        }
    }

    /**
     * 是否有恶魔卡
     */
    suspend fun hasDemonCard(): ApiResult<BooleanResult> {
        return request {
            apiMTime.postHasDemonCard()
        }
    }

    /**
     * 我/TA的套装
     */
    suspend fun suitList(
        userId: Long,
        categoryId: Long,
        pageIndex: Long = 1,
        pageSize: Long = 10L
    ): ApiResult<SuitList> {
        return request(
            converter = {
                it.apply {
                    suitList?.forEach { suit ->
                        suit.suitCategoryId = categoryId
                    }
                }
            }
        ) {
            apiMTime.postSuitList(userId, categoryId, pageIndex, pageSize)
        }
    }

    /**
     * 全部套装列表
     * suitType: 1普通套装，2限量套装
     */
    suspend fun allSuitList(
        suitType: Long = 1L,
        pageIndex: Long = 1L,
        pageSize: Long = 30L
    ): ApiResult<SuitList> {
        return request {
            apiMTime.postAllSuitList(suitType, pageIndex, pageSize)
        }
    }

    /**
     * 挖宝箱
     */
    suspend fun digBox(): ApiResult<DigBox> {
        return request {
            apiMTime.postDigBox()
        }
    }

    /**
     * 打开宝箱
     */
    suspend fun openBox(
        position: Long,
        openWithGold: Boolean = false
    ): ApiResult<OpenBox> {
        return request {
            apiMTime.postOpenBox(position, openWithGold)
        }
    }

    /**
     * 从我的开放口袋拾取卡片
     */
    suspend fun pickCardFromMe(userCardId: String): ApiResult<PickCardFromMe> {
        return request {
            apiMTime.postPickCardFromMe(userCardId)
        }
    }

    /**
     * 从好友/机器人的开放口袋拾取卡片
     */
    suspend fun pickCardFromFriend(
        userCardId: Long,
        friendId: Long,
        isRobot: Boolean
    ): ApiResult<PickCard> {
        return request {
            apiMTime.postPickCardFromFriend(userCardId, friendId, isRobot)
        }
    }

    /**
     * 从我的开放口袋批量拾取卡片
     */
    suspend fun batchPickCardFromMe(userCardIds: String): ApiResult<PickCardFromMe> {
        return request {
            apiMTime.postBatchPickCardFromMe(userCardIds)
        }
    }

    /**
     * 从好友/机器人的开放口袋批量拾取卡片
     */
    suspend fun batchPickCardFromFriend(
        userCardIds: String,
        friendId: Long,
        isRobot: Boolean
    ): ApiResult<PickCard> {
        return request {
            apiMTime.postBatchPickCardFromFriend(userCardIds, friendId, isRobot)
        }
    }

    /**
     * 从许愿树拾取卡片
     */
    suspend fun pickCardFromWish(): ApiResult<CommResult> {
        return request {
            apiMTime.postPickCardFromWish()
        }
    }

    /**
     * 开通保险箱空位1-10
     */
    suspend fun activeStrongBoxPosition(
        @IntRange(
            from = 0,
            to = 10
        ) position: Int
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postActiveStrongBoxPosition(position.toLong())
        }
    }

    /**
     * 解锁保险箱(1-10)
     */
    suspend fun unlockStrongBox(@IntRange(from = 0, to = 10) position: Int): ApiResult<CommResult> {
        return request {
            apiMTime.postUnlockStrongBox(position.toLong())
        }
    }

    /**
     * 移动卡片到保险箱
     */
    suspend fun moveCardToStrongBox(userCardIds: String): ApiResult<MoveCard> {
        return request {
            apiMTime.postMoveCardToStrongBox(userCardIds)
        }
    }

    /**
     * 移动卡片到固定口袋
     */
    suspend fun moveCardToPocket(userCardIds: String): ApiResult<MoveCard> {
        return request {
            apiMTime.postMoveCardToPocket(userCardIds)
        }
    }

    /**
     * 合成套装
     */
    suspend fun mixSuit(userCardIds: String): ApiResult<MixSuit> {
        return request {
            apiMTime.postMixSuit(userCardIds)
        }
    }

    /**
     * 丢弃卡片
     */
    suspend fun discard(
        userCardIds: String,
        friendId: Long,
        isRobot: Boolean
    ): ApiResult<Discard> {
        return request {
            apiMTime.postDiscard(userCardIds, friendId, isRobot)
        }
    }

    /**
     * 发起交易
     */
    suspend fun startTrade(
        friendId: Long,
        myCardId: Long,
        friendCardId: Long,
        gold: Long,
        message: String
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postStartTrade(friendId, myCardId, friendCardId, gold, message)
        }
    }

    /**
     * 添加卡友
     */
    suspend fun addFriend(
        friendId: Long,
        message: String
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postAddFriend(friendId, message)
        }
    }

    /**
     * 领取宝箱奖励
     */
    suspend fun drawBox(
        @IntRange(from = 0, to = 4) position: Int,
        cardIds: String
    ): ApiResult<DrawBox> {
        return request {
            apiMTime.postDrawBox(position.toLong(), cardIds)
        }
    }

    /**
     * 机器人主页
     */
    suspend fun robotPocket(robotId: Long): ApiResult<Robot> {
        return request {
            apiMTime.postRobotPocket(robotId)
        }
    }

    /**
     * 使用道具卡
     */
    suspend fun usePropCard(
        cardToolId: Long,
        targetUserId: Long,
        targetToolId: Long? = null,
        targetCardId: Long? = null,
        useDemonCard: Boolean? = null
    ): ApiResult<UseToolResult> {
        return request {
            apiMTime.postUsePropCard(
                cardToolId,
                targetUserId,
                targetToolId,
                targetCardId,
                useDemonCard
            )
        }
    }

    /**
     * 套装卡片列表
     */
    suspend fun suitCards(
        suitId: Long,
    ): ApiResult<SuitCards> {
        return request(
            converter = {
                it.apply {
                    suitInfo?.suitType = suitType
                }
            }
        ) {
            apiMTime.postSuitCards(suitId)
        }
    }

    /**
     * 套装详情弹层
     * [suitClass] 套装级别：c,b,a
     */
    suspend fun suitDetail(
        suitId: Long,
        suitClass: String,
        suitUserId: Long
    ): ApiResult<SuitDetail> {
        return request {
            apiMTime.postSuitDetail(suitId, suitClass, suitUserId)
        }
    }

    /**
     * 升级套装
     * [suitClass] 套装级别：c,b,a
     */
    suspend fun upgradeSuit(
        suitId: Long,
        suitClass: String
    ): ApiResult<UpgradeSuit> {
        return request {
            apiMTime.postUpgradeSuit(suitId, suitClass)
        }
    }

    /**
     * 按名称模糊搜索套装
     * [getCount] 获取数量（默认10）
     */
    suspend fun querySuitByCard(
        suitName: String,
        getCount: Long
    ): ApiResult<QuerySuitList> {
        return request {
            apiMTime.postQuerySuitByCard(suitName, getCount)
        }
    }

    /**
     * 某套装下我的卡片信息
     */
    suspend fun myCardBySuit(
        suitId: Long
    ): ApiResult<MyCardsBySuit> {
        return request {
            apiMTime.postMyCardBySuit(suitId)
        }
    }

    /**
     * 我的愿望
     */
    suspend fun myWish(): ApiResult<MyWish> {
        return request {
            apiMTime.postMyWish()
        }
    }

    /**
     * TA的愿望
     */
    suspend fun friendWish(friendId: Long): ApiResult<FriendWish> {
        return request {
            apiMTime.postFriendWish(friendId)
        }
    }

    /**
     * TA的愿望
     */
    suspend fun wishWall(pageIndex: Long,pageSize: Long): ApiResult<WishWall> {
        return request {
            apiMTime.getWishWall(pageIndex,pageSize)
        }
    }

    /**
     * 添加或更换许愿
     */
    suspend fun addOrChangeWish(
        cardId: Long,
        content: String
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postAddOrChangeWish(cardId, content)
        }
    }

    /**
     * 帮TA实现愿望
     */
    suspend fun wishComeTrue(friendId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postWishComeTrue(friendId)
        }
    }

    /**
     * 更新签名档
     */
    suspend fun updateSignature(signature: String): ApiResult<CommResult> {
        return request {
            apiMTime.postUpdateSignature(signature)
        }
    }

    /**
     * 最新的签名档列表
     * [getCount] 获取数量（默认20）
     */
    suspend fun signatureList(getCount: Long = 20L): ApiResult<SignatureList> {
        return request {
            apiMTime.postSignatureList(getCount)
        }
    }

    /**
     * 我的卡友
     */
    suspend fun friends(
        orderType: Long,
        friendName: String? = "",
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<Friends> {
        return request {
            apiMTime.postFriends(orderType, friendName ?: "", pageIndex, pageSize)
        }
    }

    /**
     * 获取限量套装排行
     */
    suspend fun limitSuitUsers(suitId: Long): ApiResult<LimitSuitUsers> {
        return request {
            apiMTime.postLimitSuitUsers(suitId)
        }
    }

    /**
     * 获取拍卖行购买列表
     */
    suspend fun loadAuctionBuyList(
        orderType: Long,
        queryType: Long,
        cardId: Long,
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<BuyList> {
        return request {
            apiMTime.postBuyList(orderType, queryType, cardId, pageIndex, pageSize)
        }
    }

    /**
     * 获取拍卖行拍卖列表
     */
    suspend fun loadAuctionList(
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<AuctionList> {
        return request {
            apiMTime.postAuctionList(pageIndex, pageSize)
        }
    }

    /**
     * 获取卡片商店宝箱列表
     */
    suspend fun loadBoxList(): ApiResult<BoxList> {
        return request {
            apiMTime.postBoxList()
        }
    }

    /**
     * 获取卡片商店道具卡列表
     */
    suspend fun loadPropCardList(): ApiResult<PropList> {
        return request {
            apiMTime.postPropCardList() // getStorePropList()
        }
    }

    /**
     * 获取游戏信息
     */
    suspend fun loadGameInfoList(
        type: Long,
        pageIndex: Long,
        pageSize: Long,
        timeType:Long
    ): ApiResult<RecordList> {
        return request {
            apiMTime.postRecordList(type, pageIndex, pageSize, timeType)
        }
    }

    /**
     * 获取游戏信息
     */
    suspend fun deleteRecord(type: Long): ApiResult<CommResult> {
        return request {
            apiMTime.getDeleteRecords(type.toString())
        }
    }

    /**
     * 删除卡友
     */
    suspend fun deleteFriend(friendId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postDeleteFriend(friendId)
        }
    }

    /**
     * 购买宝箱
     */
    suspend fun buyCardBox(cardBoxId: Long): ApiResult<OpenBox> {
        return request {
            apiMTime.getBuyCardBox(cardBoxId)
        }
    }

    /**
     * 购买道具卡
     */
    suspend fun buyPropCard(propCardId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.buyToolsCard(propCardId)
        }
    }


    /**
     * 批量购买道具卡
     */
    suspend fun buyBatchPropCard(propCardId: Long, num: Long): ApiResult<CommResult> {
        return request {
            apiMTime.buyBatchToolsCard(propCardId,num)
        }
    }

    /**
     * 拍卖行取回卡片
     */
    suspend fun retrieveCard(cardId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postPickCardFromAuction(cardId)
        }
    }

    /**
     * 取消拍卖卡片
     */
    suspend fun cancelAuction(cardId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postCancelAuction(cardId)
        }
    }

    /**
     * 获取竞价的列表
     */
    suspend fun getBidList(pageSize: Long): ApiResult<BidList> {
        return request {
            apiMTime.postBidList(pageSize, 20)
        }
    }

    /**
     * 发起竞价
     */
    suspend fun bid(
        auctionId: Long,
        bidPrice: Long
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postBid(auctionId, bidPrice)
        }
    }

    /**
     * 一口价
     */
    suspend fun bidSuccessPickCard(auctionId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postPickCardFromBid(auctionId)
        }
    }

    /**
     * 添加拍卖
     */
    suspend fun addAuction(
        id: Long? = null,
        type: Long? = null,
        timeLimited: Long,
        startPrice: Long,
        fixPrice: Long
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postAddAuction(id, type, timeLimited, startPrice, fixPrice)
        }
    }

    /**
     * 添加拍卖
     */
    suspend fun addTradePrice(
        recordDetailId: Long,
        raiseGolds: Long,
        message: String
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postTradePrice(recordDetailId, raiseGolds, message)
        }
    }

    /**
     * 同意添加卡友
     */
    suspend fun agreeAddFriend(recordDetailId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postAgreeFriend(recordDetailId)
        }
    }

    /**
     * 同意添加卡友
     */
    suspend fun refuseAddFriend(
        recordDetailId: Long,
        message: String
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postRefuseFriend(recordDetailId, message)
        }
    }

    /**
     * 同意添加卡友
     */
    suspend fun refuseTrade(
        recordDetailId: Long,
        message: String
    ): ApiResult<CommResult> {
        return request {
            apiMTime.postRefuseTrade(recordDetailId, message)
        }
    }

    /**
     * 同意添加卡友
     */
    suspend fun agreeTrade(recordDetailId: Long): ApiResult<CommResult> {
        return request {
            apiMTime.postAgreeTrade(recordDetailId)
        }
    }

    /**
     * 用户详细信息
     */
    suspend fun userDetail(userId: Long): ApiResult<UserDetail> {
        return request {
            apiMTime.getUserDetail(userId)
        }
    }

    /**
     * 本期发行
     */
    suspend fun currentIssueSuitList(): ApiResult<CurrentIssueSuitList> {
        return request {
            apiMTime.getCurrentIssueSuitList()
        }
    }

    /**
     * 套装展示
     */
    suspend fun suitShow(
        cardUserSuitId: Long,
        suitId: Long
    ): ApiResult<SuitShow> {
        return request {
            apiMTime.postSuitShow(cardUserSuitId, suitId)
        }
    }

    /**
     * 我的套装搜索结果列表
     */
    suspend fun searchSuitList(
        userId: Long,
        suitId: Long
    ): ApiResult<SearchSuitList> {
        return request {
            apiMTime.postSearchSuitList(userId, suitId)
        }
    }

}