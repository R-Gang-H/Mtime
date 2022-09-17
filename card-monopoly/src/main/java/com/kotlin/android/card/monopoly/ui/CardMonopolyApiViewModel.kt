package com.kotlin.android.card.monopoly.ui

import androidx.annotation.IntRange
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.card.monopoly.repository.CardMonopolyApiRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.monopoly.*

/**
 *
 * Created on 2020/6/18.
 *
 * @author o.s
 */
class CardMonopolyApiViewModel : BaseViewModel() {
    private val repo by lazy { CardMonopolyApiRepository() }

    private val shareModel by lazy { BaseUIModel<CommonShare>() }
    private val friendPocketModel by lazy { BaseUIModel<FriendPocket>() }
    private val myPocketModel by lazy { BaseUIModel<MyPocket>() }
    private val myPocketCardsModel by lazy { BaseUIModel<MyPocketCards>() }
    private val friendNormalCardsModel by lazy { BaseUIModel<FriendNormalCards>() }
    private val myPropCardsModel by lazy { BaseUIModel<MyPropCards>() }
    private val suitListModel by lazy { BaseUIModel<SuitList>() }
    private val allSuitListModel by lazy { BaseUIModel<SuitList>() }
    private val digBoxModel by lazy { BaseUIModel<DigBox>() }
    private val openBoxModel by lazy { BaseUIModel<OpenBox>() }
    private val pickCardFromMeModel by lazy { BaseUIModel<PickCardFromMe>() }
    private val pickCardFromFriendModel by lazy { BaseUIModel<PickCard>() }
    private val pickCardFromWishModel by lazy { BaseUIModel<CommResult>() }
    private val activeStrongBoxPositionModel by lazy { BaseUIModel<CommResult>() }
    private val unlockStrongBoxModel by lazy { BaseUIModel<CommResult>() }
    private val moveCardToStrongBoxModel by lazy { BaseUIModel<MoveCard>() }
    private val moveCardToPocketModel by lazy { BaseUIModel<MoveCard>() }
    private val mixSuitModel by lazy { BaseUIModel<MixSuit>() }
    private val discardModel by lazy { BaseUIModel<Discard>() }
    private val startTradeModel by lazy { BaseUIModel<CommResult>() }
    private val addFriendModel by lazy { BaseUIModel<CommResult>() }
    private val drawBoxModel by lazy { BaseUIModel<DrawBox>() }
    private val robotPocketModel by lazy { BaseUIModel<Robot>() }
    private val usePropCardModel by lazy { BaseUIModel<UseToolResult>() }
    private val suitCardsModel by lazy { BaseUIModel<SuitCards>() }
    private val suitDetailModel by lazy { BaseUIModel<SuitDetail>() }
    private val upgradeSuitModel by lazy { BaseUIModel<UpgradeSuit>() }
    private val querySuitByCardModel by lazy { BaseUIModel<QuerySuitList>() }
    private val myCardsBySuitModel by lazy { BaseUIModel<MyCardsBySuit>() }
    private val myWishModel by lazy { BaseUIModel<MyWish>() }
    private val friendWishModel by lazy { BaseUIModel<FriendWish>() }
    private val addOrChangeWishModel by lazy { BaseUIModel<CommResult>() }
    private val wishComeTrueModel by lazy { BaseUIModel<CommResult>() }
    private val updateSignatureModel by lazy { BaseUIModel<CommResult>() }
    private val signatureListModel by lazy { BaseUIModel<SignatureList>() }
    private val friendsModel by lazy { BaseUIModel<Friends>() }
    private val deleteFriendModel by lazy { BaseUIModel<CommResult>() }
    private val limitSuitUsersModel by lazy { BaseUIModel<LimitSuitUsers>() }
    private val auctionBuyModel by lazy { BaseUIModel<BuyList>() }
    private val auctionModel by lazy { BaseUIModel<AuctionList>() }
    private val boxCardModel by lazy { BaseUIModel<BoxList>() }
    private val propCardStoreModel by lazy { BaseUIModel<PropList>() }
    private val gameModel by lazy { BaseUIModel<RecordList>() }
    private val deleteRecordModel by lazy { BaseUIModel<CommResult>() }
    private val buyCardBoxModel by lazy { BaseUIModel<OpenBox>() }
    private val buyPropCardModel by lazy { BaseUIModel<CommResult>() }
    private val buyBatchPropCardModel by lazy { BaseUIModel<CommResult>() }
    private val retrieveCardModel by lazy { BaseUIModel<CommResult>() }
    private val cancelAuctionModel by lazy { BaseUIModel<CommResult>() }
    private val bidListModel by lazy { BaseUIModel<BidList>() }
    private val bidModel by lazy { BaseUIModel<CommResult>() }
    private val bidSuccessPickCardModel by lazy { BaseUIModel<CommResult>() }
    private val addAuctionModel by lazy { BaseUIModel<CommResult>() }
    private val tradeRaisePriceModel by lazy { BaseUIModel<CommResult>() }
    private val agreeAddFriendModel by lazy { BaseUIModel<CommResult>() }
    private val refuseAddFriendModel by lazy { BaseUIModel<CommResult>() }
    private val refuseTradeModel by lazy { BaseUIModel<CommResult>() }
    private val agreeTradeModel by lazy { BaseUIModel<CommResult>() }
    private val demonCardModel by lazy { BaseUIModel<BooleanResult>() }
    private val wishWallModel by lazy { BaseUIModel<WishWall>() }
    private val userDetailModel by lazy { BaseUIModel<UserDetail>() }
    private val currentIssueSuitListModel by lazy { BaseUIModel<CurrentIssueSuitList>() }
    private val suitShowModel by lazy { BaseUIModel<SuitShow>() }
    private val searchSuitListModel by lazy { BaseUIModel<SearchSuitList>() }

    val shareUiState by lazy { shareModel.uiState }
    val friendPocketUiState by lazy { friendPocketModel.uiState }
    val myPocketUiState by lazy { myPocketModel.uiState }
    val myPocketCardsUiState by lazy { myPocketCardsModel.uiState }
    val friendNormalCardsUiState by lazy { friendNormalCardsModel.uiState }
    val myPropCardsUiState by lazy { myPropCardsModel.uiState }
    val suitListUiState by lazy { suitListModel.uiState }
    val allSuitListUiState by lazy { allSuitListModel.uiState }
    val digBoxUiState by lazy { digBoxModel.uiState }
    val openBoxUiState by lazy { openBoxModel.uiState }
    val pickCardFromMeUiState by lazy { pickCardFromMeModel.uiState }
    val pickCardFromFriendUiState by lazy { pickCardFromFriendModel.uiState }
    val pickCardFromWishUiState by lazy { pickCardFromWishModel.uiState }
    val activeStrongBoxPositionUiState by lazy { activeStrongBoxPositionModel.uiState }
    val unlockStrongBoxUiState by lazy { unlockStrongBoxModel.uiState }
    val moveCardToStrongBoxUiState by lazy { moveCardToStrongBoxModel.uiState }
    val moveCardToPocketUiState by lazy { moveCardToPocketModel.uiState }
    val mixSuitUiState by lazy { mixSuitModel.uiState }
    val discardUiState by lazy { discardModel.uiState }
    val startTradeUiState by lazy { startTradeModel.uiState }
    val addFriendUiState by lazy { addFriendModel.uiState }
    val drawBoxModelUiState by lazy { drawBoxModel.uiState }
    val robotPocketUiState by lazy { robotPocketModel.uiState }
    val usePropCardUiState by lazy { usePropCardModel.uiState }
    val suitCardsUiState by lazy { suitCardsModel.uiState }
    val suitDetailUiState by lazy { suitDetailModel.uiState }
    val upgradeSuitUiState by lazy { upgradeSuitModel.uiState }
    val querySuitByCardUiState by lazy { querySuitByCardModel.uiState }
    val myCardsBySuitUiState by lazy { myCardsBySuitModel.uiState }
    val myWishUiState by lazy { myWishModel.uiState }
    val friendWishUiState by lazy { friendWishModel.uiState }
    val addOrChangeWishUiState by lazy { addOrChangeWishModel.uiState }
    val wishComeTrueUiState by lazy { wishComeTrueModel.uiState }
    val updateSignatureUiState by lazy { updateSignatureModel.uiState }
    val signatureListUiState by lazy { signatureListModel.uiState }
    val friendsUiState by lazy { friendsModel.uiState }
    val deleteFriendUiState by lazy { deleteFriendModel.uiState }
    val limitSuitUsersUiState by lazy { limitSuitUsersModel.uiState }
    val auctionBuyUiState by lazy { auctionBuyModel.uiState }
    val auctionUiState by lazy { auctionModel.uiState }
    val boxUiState by lazy { boxCardModel.uiState }
    val propCardStoreUiState by lazy { propCardStoreModel.uiState }
    val gameUiState by lazy { gameModel.uiState }
    val deleteRecordUiState by lazy { deleteRecordModel.uiState }
    val buyCardBoxUiState by lazy { buyCardBoxModel.uiState }
    val buyPropCardUiState by lazy { buyPropCardModel.uiState }
    val buyBatchPropCardUiState by lazy { buyBatchPropCardModel.uiState }
    val retrieveCardUiState by lazy { retrieveCardModel.uiState }
    val bidListUiState by lazy { bidListModel.uiState }
    val cancelAuctionUiState by lazy { cancelAuctionModel.uiState }
    val bidUiState by lazy { bidModel.uiState }
    val bidSuccessPickCardUiState by lazy { bidSuccessPickCardModel.uiState }
    val addAuctionUiState by lazy { addAuctionModel.uiState }
    val tradeRaisePriceUiState by lazy { tradeRaisePriceModel.uiState }
    val agreeAddFriendUiState by lazy { agreeAddFriendModel.uiState }
    val refuseAddFriendUiState by lazy { refuseAddFriendModel.uiState }
    val refuseTradeUiState by lazy { refuseTradeModel.uiState }
    val agreeTradeUiState by lazy { agreeTradeModel.uiState }
    val demonCardUiState by lazy { demonCardModel.uiState }
    val wishWallUiState by lazy { wishWallModel.uiState }
    val userDetailUiState by lazy { userDetailModel.uiState }
    val currentIssueSuitListUiState by lazy { currentIssueSuitListModel.uiState }
    val suitShowUiState by lazy { suitShowModel.uiState }
    val searchSuitListUiState by lazy { searchSuitListModel.uiState }


    /**
     * 分享卡片大富翁信息
     */
    fun getShareInfo() {
        call(shareModel) {
            repo.getShareInfo()
        }
    }

    /**
     * TA的主页
     */
    fun friendPocket(friendId: Long) {
        call(friendPocketModel) {
            repo.friendPocket(friendId)
        }
    }

    /**
     * 我的主页
     */
    fun myPocket() {
        call(myPocketModel) {
            repo.myPocket()
        }
    }

    /**
     * 我的口袋卡片列表
     */
    fun myPocketCards() {
        call(myPocketCardsModel) {
            repo.myPocketCards()
        }
    }

    /**
     * 我的口袋卡片列表
     */
    fun friendNormalCards(friendId: Long) {
        call(friendNormalCardsModel) {
            repo.friendNormalCards(friendId)
        }
    }

    /**
     * 我的口袋限量卡片列表
     */
    fun friendLimitCards(friendId: Long) {
        call(friendNormalCardsModel) {
            repo.friendLimitCards(friendId)
        }
    }

    /**
     * 我的道具卡
     */
    fun myPropCards() {
        call(myPropCardsModel) {
            repo.myPropCards()
        }
    }

    /**
     * 是否有恶魔卡
     */
    fun hasDemonCard() {
        call(demonCardModel) {
            repo.hasDemonCard()
        }
    }

    /**
     * 我/TA的套装
     */
    fun suitList(
        userId: Long,
        categoryId: Long,
        pageIndex: Long = 1,
        pageSize: Long = 10L
    ) {
        call(
            uiModel = suitListModel,
            hasMore = {
                it.hasMore
            }
        ) {
            repo.suitList(userId, categoryId, pageIndex, pageSize)
        }
    }

    /**
     * 全部套装列表
     * suitType: 1普通套装，2限量套装
     */
    fun allSuitList(
        suitType: Long = 1L,
        pageIndex: Long = 1L,
        pageSize: Long = 30L
    ) {
        call(
            uiModel = allSuitListModel,
            hasMore = {
                it.hasMore
            }
        ) {
            repo.allSuitList(suitType, pageIndex, pageSize)
        }
    }

    /**
     * 挖宝箱
     */
    fun digBox() {
        call(digBoxModel) {
            repo.digBox()
        }
    }

    /**
     * 打开宝箱
     */
    fun openBox(
        position: Long,
        openWithGold: Boolean = false
    ) {
        call(openBoxModel) {
            repo.openBox(position, openWithGold)
        }
    }

    /**
     * 从我的开放口袋拾取卡片
     */
    fun pickCardFromMe(userCardId: String) {
        call(pickCardFromMeModel) {
            repo.pickCardFromMe(userCardId)
        }
    }

    /**
     * 从好友/机器人的开放口袋拾取卡片
     */
    fun pickCardFromFriend(
        userCardId: Long,
        friendId: Long,
        isRobot: Boolean
    ) {
        call(pickCardFromFriendModel) {
            repo.pickCardFromFriend(userCardId, friendId, isRobot)
        }
    }

    /**
     * 从我的开放口袋批量拾取卡片
     */
    fun batchPickCardFromMe(userCardIds: String) {
        call(pickCardFromMeModel) {
            repo.batchPickCardFromMe(userCardIds)
        }
    }

    /**
     * 从好友/机器人的开放口袋批量拾取卡片
     */
    fun batchPickCardFromFriend(
        userCardIds: String,
        friendId: Long,
        isRobot: Boolean
    ) {
        call(pickCardFromFriendModel) {
            repo.batchPickCardFromFriend(userCardIds, friendId, isRobot)
        }
    }

    /**
     * 从许愿树拾取卡片
     */
    fun pickCardFromWish() {
        call(pickCardFromWishModel) {
            repo.pickCardFromWish()
        }
    }

    /**
     * 开通保险箱空位1-10
     */
    fun activeStrongBoxPosition(@IntRange(from = 0, to = 10) position: Int) {
        call(activeStrongBoxPositionModel) {
            repo.activeStrongBoxPosition(position)
        }
    }

    /**
     * 解锁保险箱(1-10)
     */
    fun unlockStrongBox(@IntRange(from = 0, to = 10) position: Int) {
        call(unlockStrongBoxModel) {
            repo.unlockStrongBox(position)
        }
    }

    /**
     * 移动卡片到保险箱
     */
    fun moveCardToStrongBox(userCardIds: String) {
        call(moveCardToStrongBoxModel) {
            repo.moveCardToStrongBox(userCardIds)
        }
    }

    /**
     * 移动卡片到固定口袋
     */
    fun moveCardToPocket(userCardIds: String) {
        call(moveCardToPocketModel) {
            repo.moveCardToPocket(userCardIds)
        }
    }

    /**
     * 合成套装
     */
    fun mixSuit(userCardIds: String) {
        call(mixSuitModel) {
            repo.mixSuit(userCardIds)
        }
    }

    /**
     * 丢弃卡片
     */
    fun discard(
        userCardIds: String,
        friendId: Long,
        isRobot: Boolean
    ) {
        call(discardModel) {
            repo.discard(userCardIds, friendId, isRobot)
        }
    }

    /**
     * 发起交易
     */
    fun startTrade(
        friendId: Long,
        myCardId: Long,
        friendCardId: Long,
        gold: Long,
        message: String
    ) {
        call(startTradeModel) {
            repo.startTrade(friendId, myCardId, friendCardId, gold, message)
        }
    }

    /**
     * 添加卡友
     */
    fun addFriend(
        friendId: Long,
        message: String
    ) {
        call(addFriendModel) {
            repo.addFriend(friendId, message)
        }
    }

    /**
     * 领取宝箱奖励
     */
    fun drawBox(
        @IntRange(from = 0, to = 4) position: Int,
        cardIds: String
    ) {
        call(uiModel = drawBoxModel,
            isShowLoading = true,
            isEmpty = {
                false
            }
        ) {
            repo.drawBox(position, cardIds)
        }
    }

    /**
     * 机器人主页
     */
    fun robotPocket(robotId: Long) {
        call(robotPocketModel) {
            repo.robotPocket(robotId)
        }
    }

    /**
     * 使用道具卡
     * @param cardToolId 要使用的道具卡Id
     * @param targetUserId 目标用户Id（对谁使用）
     * @param targetToolId 目标道具卡Id（使用复制卡时必传此参数）
     * @param targetCardId 目标卡片Id（使用打劫卡时必传此参数）
     * @param useDemonCard 是否使用恶魔卡（使用奴隶卡或黑客卡时必传此参数）
     */
    fun usePropCard(
        cardToolId: Long,
        targetUserId: Long,
        targetToolId: Long? = null,
        targetCardId: Long? = null,
        useDemonCard: Boolean? = null
    ) {
        call(usePropCardModel) {
            repo.usePropCard(cardToolId, targetUserId, targetToolId, targetCardId, useDemonCard)
        }
    }

    /**
     * 套装卡片列表
     */
    fun suitCards(
        suitId: Long
    ) {
        call(suitCardsModel) {
            repo.suitCards(suitId)
        }
    }

    /**
     * 套装详情弹层
     * [suitClass] 套装级别：c,b,a
     */
    fun suitDetail(
        suitId: Long,
        suitClass: String,
        suitUserId: Long
    ) {
        call(suitDetailModel) {
            repo.suitDetail(suitId, suitClass, suitUserId)
        }
    }

    /**
     * 升级套装
     * [suitClass] 套装级别：c,b,a
     */
    fun upgradeSuit(
        suitId: Long,
        suitClass: String
    ) {
        call(upgradeSuitModel) {
            repo.upgradeSuit(suitId, suitClass)
        }
    }

    /**
     * 按名称模糊搜索套装
     * [getCount] 获取数量（默认10）
     */
    fun querySuitByCard(
        suitName: String,
        getCount: Long = 10L,
        isShowLoading: Boolean
    ) {
        call(
            uiModel = querySuitByCardModel,
            isShowLoading = isShowLoading
        ) {
            repo.querySuitByCard(suitName, getCount)
        }
    }

    /**
     * 某套装下我的卡片信息
     */
    fun myCardBySuit(
        suitId: Long
    ) {
        call(myCardsBySuitModel) {
            repo.myCardBySuit(suitId)
        }
    }

    /**
     * 我的愿望
     */
    fun myWish() {
        call(myWishModel) {
            repo.myWish()
        }
    }

    /**
     * TA的愿望
     */
    fun friendWish(friendId: Long) {
        call(friendWishModel) {
            repo.friendWish(friendId)
        }
    }

    /**
     * 许愿墙
     */
    fun wishWall(pageIndex: Long,pageSize: Long) {
        call(wishWallModel) {
            repo.wishWall(pageIndex,pageSize)
        }
    }

    /**
     * 添加或更换许愿
     */
    fun addOrChangeWish(
        cardId: Long,
        content: String
    ) {
        call(addOrChangeWishModel) {
            repo.addOrChangeWish(cardId, content)
        }
    }

    /**
     * 帮TA实现愿望
     */
    fun wishComeTrue(friendId: Long) {
        call(wishComeTrueModel) {
            repo.wishComeTrue(friendId)
        }
    }

    /**
     * 更新签名档
     */
    fun updateSignature(signature: String) {
        call(updateSignatureModel) {
            repo.updateSignature(signature)
        }
    }

    /**
     * 最新的签名档列表
     * [getCount] 获取数量（默认20）
     */
    fun signatureList(getCount: Long = 200L) {
        call(signatureListModel) {
            repo.signatureList(getCount)
        }
    }


    /**
     * 我的卡友
     */
    fun friends(
        orderType: Long,
        friendName: String? = "",
        pageIndex: Long,
        pageSize: Long = 20L
    ) {
        call(
            uiModel = friendsModel,
            hasMore = {
                it.hasMore
            }
        ) {
            repo.friends(orderType, friendName, pageIndex, pageSize)
        }
    }

    /**
     * 删除卡友
     */
    fun deleteFriend(friendId: Long) {
        call(deleteFriendModel) {
            repo.deleteFriend(friendId)
        }
    }

    /**
     * 展示限量套装排行列表
     * @param suitId 套装id
     */
    fun loadSuitRankList(suitId: Long) {
        call(
            uiModel = limitSuitUsersModel,
            isEmpty = {
                it.maxinumUser == null
            }
        ) {
            repo.limitSuitUsers(suitId)
        }
    }

    /**
     * 拍卖行展示购买列表
     * @param orderType 1结束时间倒序，2结束时间正序，3一口价倒序，4一口价正序，5当前价倒序，6当前价正序
     * @param queryType  搜索类型 1卡片，2道具卡，3套装
     * @param cardId 指定的卡片Id
     */
    fun loadAuctionBuyList(
        orderType: Long,
        queryType: Long,
        cardId: Long,
        pageIndex: Long,
        pageSize: Long
    ) {
        call(
            uiModel = auctionBuyModel,
            isEmpty = {
                it.buyList?.isEmpty() == true && pageIndex == 1L
            }
        ) {
            repo.loadAuctionBuyList(orderType, queryType, cardId, pageIndex, pageSize)
        }
    }

    /**
     * 拍卖行展示已经拍卖的列表
     */
    fun loadAuctionList(
        pageIndex: Long,
        pageSize: Long
    ) {
        call(
            uiModel = auctionModel,
            isEmpty = {
                pageIndex == 1L && it.auctionList?.isEmpty() == true
            },
            hasMore = {
                it.hasMore
            }
        ) {
            repo.loadAuctionList(pageIndex, pageSize)
        }
    }

    /**
     * 卡片商店展示宝箱
     */
    fun loadBoxList() {
        call(
            uiModel = boxCardModel,
            isEmpty = {
                it.commonBoxList?.isNullOrEmpty() == true &&
                        it.activityBoxList?.isNullOrEmpty() == true &&
                        it.boughtBox == null &&
                        it.limitBoxList?.isNullOrEmpty() == true
            }
        ) {
            repo.loadBoxList()
        }
    }

    /**
     * 卡片商店展示道具卡
     */
    fun loadPropCardList() {
        call(
            uiModel = propCardStoreModel,
            isEmpty = {
                it.toolCardList?.isNullOrEmpty() == true
            }
        ) {
            repo.loadPropCardList()
        }
    }

    /**
     * 游戏信息加载
     */
    fun loadGameInfo(typeId: Long, pageIndex: Long, pageSize: Long, type:Long) {
        call(gameModel) {
            repo.loadGameInfoList(typeId, pageIndex, pageSize, type)
        }
    }

    /**
     * 删除信息
     */
    fun deleteRecord(recordDetailId: Long) {
        call(deleteRecordModel) {
            repo.deleteRecord(recordDetailId)
        }
    }


    /**
     * 购买宝箱
     */
    fun buyCardBox(cardBoxId: Long) {
        call(buyCardBoxModel) {
            repo.buyCardBox(cardBoxId)
        }
    }

    /**
     * 购买道具卡
     */
    fun buyPropCard(propId: Long) {
        call(buyPropCardModel) {
            repo.buyPropCard(propId)
        }
    }

    /**
     * 购买道具卡
     */
    fun buyBatchPropCard(propId: Long,num:Long) {
        call(buyPropCardModel) {
            repo.buyBatchPropCard(propId,num)
        }
    }

    /**
     * 流拍取回卡片
     */
    fun retrieveCard(cardId: Long) {
        call(retrieveCardModel) {
            repo.retrieveCard(cardId)
        }
    }

    /**
     * 取消拍卖
     */
    fun cancelAuction(cardId: Long) {
        call(cancelAuctionModel) {
            repo.cancelAuction(cardId)
        }
    }

    /**
     * 竞价的列表
     */
    fun getAuctionBidList(pageIndex: Long) {
        call(bidListModel) {
            repo.getBidList(pageIndex)
        }
    }

    /**
     * 发起竞价
     * @param auctionId 竞价id
     * @param bidPrice 价格
     */
    fun bid(auctionId: Long, bidPrice: Long) {
        call(bidModel) {
            repo.bid(auctionId, bidPrice)
        }
    }

    /**
     * 一口价
     * @param auctionId 竞价id
     */
    fun bidSuccessPickCard(auctionId: Long) {
        call(bidSuccessPickCardModel) {
            repo.bidSuccessPickCard(auctionId)
        }
    }

    /**
     * 添加拍卖
     */
    fun addAuction(
        id: Long? = null,
        type: Long? = null,
        timeLimited: Long,
        startPrice: Long,
        fixPrice: Long
    ) {
        call(addAuctionModel) {
            repo.addAuction(id, type, timeLimited, startPrice, fixPrice)
        }
    }

    /**
     * 交易加价
     */
    fun addTradePrice(recordDetailId: Long, raiseGolds: Long, message: String) {
        call(tradeRaisePriceModel) {
            repo.addTradePrice(recordDetailId, raiseGolds, message)
        }
    }

    /**
     * 同意添加好友
     */
    fun agreeFriend(recordDetailId: Long) {
        call(agreeAddFriendModel) {
            repo.agreeAddFriend(recordDetailId)
        }
    }


    /**
     * 拒绝添加好友
     */
    fun refuseFriend(recordDetailId: Long, message: String) {
        call(refuseAddFriendModel) {
            repo.refuseAddFriend(recordDetailId, message)
        }
    }

    /**
     * 谢绝交易
     */
    fun refuseTrade(recordDetailId: Long, message: String) {
        call(refuseTradeModel) {
            repo.refuseTrade(recordDetailId, message)
        }
    }

    /**
     * 同意交易
     */
    fun agreeTrade(recordDetailId: Long) {
        call(agreeTradeModel) {
            repo.agreeTrade(recordDetailId)
        }
    }

    /**
     * 用户详细信息
     */
    fun userDetail(userId: Long) {
        call(userDetailModel) {
            repo.userDetail(userId)
        }
    }


    /**
     * 本期发行
     */
    fun currentIssueSuitList() {
        call(currentIssueSuitListModel) {
            repo.currentIssueSuitList()
        }
    }

    /**
     * 套装展示
     */
    fun suitShow(
        cardUserSuitId: Long,
        suitId: Long
    ) {
        call(suitShowModel) {
            repo.suitShow(cardUserSuitId, suitId)
        }
    }

    /**
     * 我的套装搜索结果列表
     */
    fun searchSuitList(
        userId: Long,
        suitId: Long
    ) {
        call(searchSuitListModel) {
            repo.searchSuitList(userId, suitId)
        }
    }

}
